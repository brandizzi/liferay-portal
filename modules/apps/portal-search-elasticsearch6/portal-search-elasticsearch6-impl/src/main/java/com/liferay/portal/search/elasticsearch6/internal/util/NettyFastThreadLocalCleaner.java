package com.liferay.portal.search.elasticsearch6.internal.util;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import io.netty.util.concurrent.FastThreadLocal;

@Component
public class NettyFastThreadLocalCleaner {

	@Deactivate
	protected void deactivate(Map<String, Object> properties) {
		if (_log.isInfoEnabled()) {
			_log.info("Cleaning up Netty.");
		}
		
		System.err.println("Cleaning up Netty. KILL IT!");
		
		FastThreadLocal.removeAll();
		FastThreadLocal.destroy();

		System.err.println("Netty KILLT!!!");
		
		if (_log.isInfoEnabled()) {
			_log.info("Netty cleaned up.");
		}
	}
	
	private static final Log _log = LogFactoryUtil.getLog(
			NettyFastThreadLocalCleaner.class);

	
	
}
