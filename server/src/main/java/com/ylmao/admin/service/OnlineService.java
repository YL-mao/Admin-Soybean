package com.ylmao.admin.service;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ylmao.admin.common.OnlineSessionKeys;
import com.ylmao.admin.config.exception.BusinessException;
import com.ylmao.admin.config.saToken.SaTokenUtil;
import com.ylmao.admin.dto.OnlineDto;
import com.ylmao.admin.dto.PageQuery;
import com.ylmao.admin.entity.User;
import com.ylmao.admin.mapper.UserMapper;
import com.ylmao.admin.vo.OnlineVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OnlineService {

    private static final int TOKEN_DISPLAY_HEAD = 8;
    private static final int TOKEN_DISPLAY_TAIL = 6;

    private final UserMapper userMapper;

    /**
     * 在线会话分页：以 Redis Token 键为主集；无 userId 时键分页再补当前页 Session，
     * 有 userId 时全键扫后按 loginId 过滤再分页。顺序跟 Sa-Token 返回序。
     */
    public IPage<OnlineVo.OnlineListVo> selectPage(PageQuery pageQuery, OnlineDto.OnlineList query) {
        String userId = query != null ? StrUtil.trim(query.userId()) : null;
        int page = pageQuery.getPage() == null ? 1 : pageQuery.getPage();
        int limit = pageQuery.getLimit() == null ? 10 : pageQuery.getLimit();
        int from = Math.max((page - 1) * limit, 0);
        String currentToken = StpUtil.getTokenValue();

        List<String> tokenValues;
        long total;
        if (StrUtil.isNotBlank(userId)) {
            if (userMapper.selectById(userId) == null) {
                Page<OnlineVo.OnlineListVo> empty = new Page<>(page, limit, 0);
                empty.setRecords(List.of());
                return empty;
            }
            // 与空条件同源扫键再滤 loginId；不用 getTokenValueListByLoginId，避免 Account-Session 漏登。
            List<String> matched = listTokenValuesByUserId(userId);
            total = matched.size();
            tokenValues = pageSlice(matched, from, limit);
        } else {
            // 先取键再切页，只给当前页补 Session，避免全量 hydrate。
            List<String> tokenKeys = searchTokenKeys();
            if (tokenKeys.isEmpty()) {
                Page<OnlineVo.OnlineListVo> empty = new Page<>(page, limit, 0);
                empty.setRecords(List.of());
                return empty;
            }
            total = tokenKeys.size();
            tokenValues = tokenValuesFromKeys(pageSlice(tokenKeys, from, limit));
        }

        List<TokenRow> rows = hydrateTokenRows(tokenValues);
        Map<String, User> userById = loadUsers(rows.stream().map(TokenRow::userId).distinct().toList());
        List<OnlineVo.OnlineListVo> records = new ArrayList<>(rows.size());
        for (TokenRow row : rows) {
            User user = userById.get(row.userId());
            records.add(new OnlineVo.OnlineListVo(
                    row.tokenValue(),
                    truncateToken(row.tokenValue()),
                    row.userId(),
                    user != null ? user.getUserAccount() : "",
                    user != null ? StrUtil.blankToDefault(user.getUserName(), "") : "",
                    StrUtil.blankToDefault(row.loginIp(), ""),
                    StrUtil.blankToDefault(row.loginTime(), ""),
                    StrUtil.blankToDefault(row.browser(), ""),
                    StrUtil.blankToDefault(row.systemOs(), ""),
                    row.timeoutSeconds(),
                    formatTimeout(row.timeoutSeconds()),
                    Objects.equals(currentToken, row.tokenValue())
            ));
        }

        Page<OnlineVo.OnlineListVo> voPage = new Page<>(page, limit, total);
        voPage.setRecords(records);
        return voPage;
    }

    /** 按 Token 强退；禁止踢当前会话。 */
    public void kickByToken(OnlineDto.OnlineKick kick) {
        String tokenValue = StrUtil.trim(kick.tokenValue());
        String currentToken = StpUtil.getTokenValue();
        if (StrUtil.equals(tokenValue, currentToken)) {
            throw new BusinessException("不能强退当前登录会话");
        }
        Object loginId = StpUtil.getLoginIdByToken(tokenValue);
        if (loginId == null) {
            throw new BusinessException("会话不存在或已下线");
        }
        StpUtil.logoutByTokenValue(tokenValue);
    }

    /** 按用户 ID 强退全部会话；在线用户页与用户列表共用该能力。 */
    public void kickByUserId(OnlineDto.OnlineKickUser kickUser) {
        String userId = StrUtil.trim(kickUser.userId());
        if (userMapper.selectById(userId) == null) {
            throw new BusinessException("用户不存在");
        }
        if (StrUtil.equals(userId, SaTokenUtil.getUserId())) {
            throw new BusinessException("不能踢出当前登录用户的全部会话");
        }
        if (!StpUtil.isLogin(userId)) {
            throw new BusinessException("用户已离线");
        }
        StpUtil.logout(userId);
    }

    /** 仅判断给定用户里谁在线，避免全站扫 Token。 */
    public Set<String> listOnlineAmong(Collection<String> userIds) {
        Set<String> online = new HashSet<>();
        if (CollectionUtils.isEmpty(userIds)) {
            return online;
        }
        for (String userId : userIds) {
            if (StrUtil.isNotBlank(userId) && StpUtil.isLogin(userId)) {
                online.add(userId);
            }
        }
        return online;
    }

    /** Redis 中 Token 键列表（Sa-Token 返回序）；空安全。 */
    private List<String> searchTokenKeys() {
        List<String> tokenKeys = StpUtil.searchTokenValue("", 0, -1, false);
        return tokenKeys == null ? List.of() : tokenKeys;
    }

    /**
     * 全站扫 Token 键后按 loginId 过滤出该用户的 tokenValue。
     * 不用 getTokenValueListByLoginId：Account-Session 登记可能少于 Redis 中仍有效的 Token。
     */
    private List<String> listTokenValuesByUserId(String userId) {
        List<String> matched = new ArrayList<>();
        for (String tokenKey : searchTokenKeys()) {
            String tokenValue = toTokenValue(tokenKey);
            if (StrUtil.isBlank(tokenValue)) {
                continue;
            }
            Object loginIdObj = StpUtil.getLoginIdByToken(tokenValue);
            if (loginIdObj != null && userId.equals(String.valueOf(loginIdObj))) {
                matched.add(tokenValue);
            }
        }
        return matched;
    }

    /** 键列表转 tokenValue，跳过空值。 */
    private List<String> tokenValuesFromKeys(List<String> tokenKeys) {
        List<String> tokenValues = new ArrayList<>(tokenKeys.size());
        for (String tokenKey : tokenKeys) {
            String tokenValue = toTokenValue(tokenKey);
            if (StrUtil.isNotBlank(tokenValue)) {
                tokenValues.add(tokenValue);
            }
        }
        return tokenValues;
    }

    private static <T> List<T> pageSlice(List<T> list, int from, int limit) {
        if (from >= list.size()) {
            return List.of();
        }
        return list.subList(from, Math.min(from + limit, list.size()));
    }

    private List<TokenRow> hydrateTokenRows(List<String> tokenValues) {
        if (CollectionUtils.isEmpty(tokenValues)) {
            return List.of();
        }
        List<TokenRow> rows = new ArrayList<>(tokenValues.size());
        for (String tokenValue : tokenValues) {
            if (StrUtil.isBlank(tokenValue)) {
                continue;
            }
            Object loginIdObj = StpUtil.getLoginIdByToken(tokenValue);
            if (loginIdObj == null) {
                continue;
            }
            String userId = String.valueOf(loginIdObj);
            SaSession tokenSession = StpUtil.getStpLogic().getTokenSessionByToken(tokenValue, false);
            rows.add(new TokenRow(
                    tokenValue,
                    userId,
                    tokenSession != null ? tokenSession.getString(OnlineSessionKeys.IP) : null,
                    tokenSession != null ? tokenSession.getString(OnlineSessionKeys.LOGIN_TIME) : null,
                    tokenSession != null ? tokenSession.getString(OnlineSessionKeys.BROWSER) : null,
                    tokenSession != null ? tokenSession.getString(OnlineSessionKeys.OS) : null,
                    StpUtil.getTokenTimeout(tokenValue)
            ));
        }
        return rows;
    }

    private Map<String, User> loadUsers(List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return Map.of();
        }
        List<User> users = userMapper.selectByIds(userIds);
        if (users == null || users.isEmpty()) {
            return Map.of();
        }
        Map<String, User> map = new HashMap<>();
        for (User user : users) {
            if (user != null && StrUtil.isNotBlank(user.getUserId())) {
                map.put(user.getUserId(), user);
            }
        }
        return map;
    }

    private String toTokenValue(String tokenKey) {
        if (StrUtil.isBlank(tokenKey)) {
            return "";
        }
        String prefix = StpUtil.getStpLogic().splicingKeyTokenValue("");
        if (StrUtil.isNotBlank(prefix) && tokenKey.startsWith(prefix)) {
            return tokenKey.substring(prefix.length());
        }
        int idx = tokenKey.lastIndexOf(':');
        return idx >= 0 ? tokenKey.substring(idx + 1) : tokenKey;
    }

    private static String truncateToken(String tokenValue) {
        if (StrUtil.isBlank(tokenValue)) {
            return "";
        }
        if (tokenValue.length() <= TOKEN_DISPLAY_HEAD + TOKEN_DISPLAY_TAIL + 3) {
            return tokenValue;
        }
        return tokenValue.substring(0, TOKEN_DISPLAY_HEAD)
                + "..."
                + tokenValue.substring(tokenValue.length() - TOKEN_DISPLAY_TAIL);
    }

    private static String formatTimeout(long seconds) {
        if (seconds == -1) {
            return "永久";
        }
        if (seconds < 0) {
            return "—";
        }
        long day = seconds / 86400;
        long hour = (seconds % 86400) / 3600;
        long minute = (seconds % 3600) / 60;
        if (day > 0) {
            return day + "天" + hour + "时";
        }
        if (hour > 0) {
            return hour + "时" + minute + "分";
        }
        if (minute > 0) {
            return minute + "分";
        }
        return seconds + "秒";
    }

    private record TokenRow(
            String tokenValue,
            String userId,
            String loginIp,
            String loginTime,
            String browser,
            String systemOs,
            long timeoutSeconds
    ) {
    }
}
