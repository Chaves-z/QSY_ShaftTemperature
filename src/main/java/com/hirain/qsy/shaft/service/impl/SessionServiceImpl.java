package com.hirain.qsy.shaft.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hirain.qsy.shaft.model.User;
import com.hirain.qsy.shaft.model.UserOnline;
import com.hirain.qsy.shaft.service.SessionService;

/**
 * Shiro Session 对象管理
 */
@Service("sessionService")
public class SessionServiceImpl implements SessionService {

	@Autowired
	private SessionDAO sessionDAO;

	@Override
	public List<UserOnline> list() {
		List<UserOnline> list = new ArrayList<>();
		Collection<Session> sessions = sessionDAO.getActiveSessions();
		for (Session session : sessions) {
			UserOnline userOnline = new UserOnline();
			User user;
			SimplePrincipalCollection principalCollection;
			if (session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY) == null) {
				continue;
			} else {
				principalCollection = (SimplePrincipalCollection) session.getAttribute(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
				user = (User) principalCollection.getPrimaryPrincipal();
				userOnline.setUsername(user.getUsername());
				userOnline.setUserId(user.getId().toString());
				userOnline.setRealName(user.getName());
			}
			userOnline.setId((String) session.getId());
			userOnline.setHost(session.getHost());
			userOnline.setStartTimestamp(session.getStartTimestamp());
			userOnline.setLastAccessTime(session.getLastAccessTime());
			long timeout = session.getTimeout();
			userOnline.setStatus(timeout == 0L ? "0" : "1");
			userOnline.setTimeout(timeout);
			list.add(userOnline);
		}
		return list;
	}

	@Override
	public boolean forceLogout(String sessionId) {
		Session session = sessionDAO.readSession(sessionId);
		session.setTimeout(0);
		session.stop();
		sessionDAO.delete(session);
		return true;
	}

}
