package org.xhome.xfileupload.web.listener;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.xhome.xfileupload.core.service.FileContentService;
import org.xhome.xfileupload.web.util.FileUploadUtils;

/**
 * @project xfileupload-web
 * @author jhat
 * @email cpf624@126.com
 * @homepage http://pfchen.org
 * @date Mar 17, 2014
 * @describe
 */
public class FileUploadCleanupListener implements HttpSessionListener {

    private FileContentService fileContentService;

    /**
     * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        fileContentService = WebApplicationContextUtils
                        .getWebApplicationContext(
                                        se.getSession().getServletContext())
                        .getBean("fileContentService", FileContentService.class);
    }

    /**
     * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        FileUploadUtils.cleanFileContent(session, fileContentService);
    }

}
