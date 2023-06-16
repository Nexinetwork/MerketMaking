/**
 *
 */
package com.plgchain.app.plingaHelper.util;

import java.io.Serializable;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

/**
 *
 */
public class ServiceUtil implements Serializable {

	private static final long serialVersionUID = -4019077067217960978L;

	public boolean restartService(String ip, int port,String privPath, String serviceName) {
		boolean result = false;
        try {
            JSch jSch = new JSch();

            jSch.addIdentity(privPath);
            Session session = jSch.getSession("root", ip, port);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            String command = "systemctl restart " + serviceName;
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.connect();

            if (channel.getExitStatus() == 0) {
            	result = true;
            } else {
            	result = false;
            }

            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
