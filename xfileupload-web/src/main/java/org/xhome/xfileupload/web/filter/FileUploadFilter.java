package org.xhome.xfileupload.web.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xhome.util.StringUtils;
import org.xhome.xauth.User;
import org.xhome.xauth.web.util.AuthUtils;
import org.xhome.xfileupload.FileContent;
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
@Component
public class FileUploadFilter implements Filter {

    @Autowired
    private FileContentService fileContentService;

    private Logger             logger = LoggerFactory.getLogger(FileUploadFilter.class);

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
     *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse res,
                    FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        User user = AuthUtils.getCurrentUser(request);
        String userName = user.getName();
        boolean allow = true;
        String url = request.getRequestURI();

        // 校验请求URL合法性
        String id = request.getParameter("id");
        if (StringUtils.isEmpty(id) || !id.matches("\\d+")) {
            allow = false;
        } else {
            // 校验数据库中是否存在对应的上传文件
            FileContent fileContent = fileContentService.getFileContent(user,
                            Integer.parseInt(id));
            if (fileContent == null || !url.endsWith(fileContent.getURL())) {
                allow = false;
            } else {
                // 校验ServletContext中是否有相同的上传文件信息
                // 如果有说明属于已上传，未处理的文件，仅允许上传用户自己访问
                List<FileContent> servletContextFileContents = FileUploadUtils
                                .getServletContextFileContents(request);
                if (servletContextFileContents != null) {
                    long fid = fileContent.getId();
                    boolean uploading = false;
                    for (FileContent content : servletContextFileContents) {
                        if (content.getId().longValue() == fid) {
                            uploading = true;
                            break;
                        }
                    }

                    if (uploading) {
                        allow = fileContent.getOwner().equals(user.getOwner());
                    } else {
                        // 上传文件访问权限控制
                    }
                }
            }
        }

        if (allow) {
            logger.info("allow {} to request {}", userName, url);
            chain.doFilter(req, res);
        } else {
            logger.warn("deny {} to request {}", userName, url);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    @Override
    public void destroy() {}

    /**
     * @return the fileContentService
     */
    public FileContentService getFileContentService() {
        return fileContentService;
    }

    /**
     * @param fileContentService
     *            the fileContentService to set
     */
    public void setFileContentService(FileContentService fileContentService) {
        this.fileContentService = fileContentService;
    }

}
