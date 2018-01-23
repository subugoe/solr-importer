package sub.ent.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Uses some Spring magic to get the current web address and port, which are also shown in the browser.
 */
public class UrlGetter {

	public String getCurrentUrl() {
		HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		// lazy about determining protocol but can be done too
		String currentUrl = "http://" + currentRequest.getLocalName();
		if (currentRequest.getLocalPort() != 80) {
			currentUrl += ":" + currentRequest.getLocalPort();
		}
		return currentUrl;
	}

}
