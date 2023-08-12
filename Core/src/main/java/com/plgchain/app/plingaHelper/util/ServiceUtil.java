/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 *
 */
public class ServiceUtil implements Serializable {

	private static final long serialVersionUID = -4019077067217960978L;
	private final static Logger logger = LoggerFactory.getLogger(ServiceUtil.class);

	public static boolean restartService(String ip, int port, String privPath, String serviceName) {
		boolean result = false;
		JSch jSch = new JSch();
		ChannelExec channel = null;
		Session session = null;
		try {
			jSch.addIdentity(privPath);
			session = jSch.getSession("root", ip, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			String command = "systemctl restart " + serviceName;
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.connect();

			if (channel.getExitStatus() == 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (JSchException e) {
			logger.error(String.format("Restart error in server %s and port %d and service %s and privateKey %s", ip,port,serviceName,privPath));
			logger.error("Server restart error",e);
		} finally {
			if (channel != null) {
				if (channel.isConnected())
					channel.disconnect();
				channel = null;
			}
			if (session != null) {
				if (session.isConnected())
					session.disconnect();
				session = null;
			}
			jSch = null;
		}
		return result;
	}

	public static boolean stopService(String ip, int port, String privPath, String serviceName) {
		boolean result = false;
		JSch jSch = new JSch();
		ChannelExec channel = null;
		Session session = null;
		try {
			jSch.addIdentity(privPath);
			session = jSch.getSession("root", ip, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			String command = "systemctl stop " + serviceName;
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.connect();

			if (channel.getExitStatus() == 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (JSchException e) {
			logger.error(String.format("System stop error in ip %s/%s and service %s", ip,port,serviceName));
		} finally {
			if (channel != null) {
				if (channel.isConnected())
					channel.disconnect();
				channel = null;
			}
			if (session != null) {
				if (session.isConnected())
					session.disconnect();
				session = null;
			}
			jSch = null;
		}
		return result;
	}

	public static boolean startService(String ip, int port, String privPath, String serviceName) {
		boolean result = false;
		JSch jSch = new JSch();
		ChannelExec channel = null;
		Session session = null;
		try {
			jSch.addIdentity(privPath);
			session = jSch.getSession("root", ip, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			String command = "systemctl start " + serviceName;
			channel = (ChannelExec) session.openChannel("exec");
			channel.setCommand(command);
			channel.connect();

			if (channel.getExitStatus() == 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (JSchException e) {
			logger.error(String.format("System start error in ip %s/%s and service %s", ip,port,serviceName));
		} finally {
			if (channel != null) {
				if (channel.isConnected())
					channel.disconnect();
				channel = null;
			}
			if (session != null) {
				if (session.isConnected())
					session.disconnect();
				session = null;
			}
			jSch = null;
		}
		return result;
	}

}
