package org.xhome.xfileupload.web.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhome.common.util.FileUtils;
import org.xhome.xauth.User;
import org.xhome.xauth.web.util.AuthUtils;
import org.xhome.xfileupload.FileContent;
import org.xhome.xfileupload.core.service.FileContentService;
import org.xhome.xfileupload.web.action.FileUploadAction;

/**
 * @project xfileupload-web
 * @author jhat
 * @email cpf624@126.com
 * @homepage http://pfchen.org
 * @date Mar 17, 2014
 * @describe 文件上传工具类
 */
@SuppressWarnings("unchecked")
public class FileUploadUtils {

    // 用户上传文件信息记录
    public final static String  FILE_CONTENTS_SESSION_KEY         = "org.xhome.xfileupload.session.filecontents";
    // 已移除的用户上传文件信息记录（已处理完成）
    public final static String  FILE_CONTENTS_REMOVED_SESSION_KEY = "org.xhome.xfileupload.session.filecontents.removed";

    private final static Logger logger                            = LoggerFactory.getLogger(FileUploadUtils.class);

    /**
     * 获取存放在Session中的已上传，未处理完成的文件信息
     * 
     * @param session
     * @return
     */
    public static List<FileContent> getSessionFileContents(HttpSession session) {
        return session != null ? (List<FileContent>) session
                        .getAttribute(FILE_CONTENTS_SESSION_KEY) : null;
    }

    /**
     * 获取存放在Session中的已上传，未处理完成的文件信息
     * 
     * @param request
     * @return
     */
    public static List<FileContent> getSessionFileContents(
                    HttpServletRequest request) {
        return request != null ? getSessionFileContents(request.getSession())
                        : null;
    }

    /**
     * 获取存放在Session中已移除的上传文件信息（已处理）
     * 
     * @param session
     * @return
     */
    public static List<FileContent> getRemovedSessionFileContents(
                    HttpSession session) {
        return session != null ? (List<FileContent>) session
                        .getAttribute(FILE_CONTENTS_REMOVED_SESSION_KEY) : null;
    }

    /**
     * 获取存放在Session中已移除的上传文件信息（已处理）
     * 
     * @param request
     * @return
     */
    public static List<FileContent> getRemovedSessionFileContents(
                    HttpServletRequest request) {
        return request != null ? getRemovedSessionFileContents(request
                        .getSession()) : null;
    }

    /**
     * 获取存放在ServletContext中的已上传，未处理完成的文件信息
     * 
     * @param session
     * @return
     */
    public static List<FileContent> getServletContextFileContents(
                    HttpSession session) {
        return session != null ? (List<FileContent>) session
                        .getServletContext().getAttribute(
                                        FILE_CONTENTS_SESSION_KEY) : null;
    }

    /**
     * 获取存放在ServletContext中的已上传，未处理完成的文件信息
     * 
     * @param request
     * @return
     */
    public static List<FileContent> getServletContextFileContents(
                    HttpServletRequest request) {
        return request != null ? getServletContextFileContents(request
                        .getSession()) : null;
    }

    /**
     * 保存用户上传的文件信息（未处理）
     * 
     * @param session
     * @param fileContent
     */
    public static void addFileContent(HttpSession session,
                    FileContent fileContent) {
        if (session != null) {
            // 在Session中保存
            List<FileContent> sessionFileContents = getSessionFileContents(session);
            if (sessionFileContents == null) {
                sessionFileContents = new ArrayList<FileContent>();
                session.setAttribute(FILE_CONTENTS_SESSION_KEY,
                                sessionFileContents);
            }
            sessionFileContents.add(fileContent);

            // 在ServletContext中保存
            List<FileContent> servletContextFileContents = getServletContextFileContents(session);
            if (servletContextFileContents == null) {
                servletContextFileContents = new ArrayList<FileContent>();
                session.getServletContext().setAttribute(
                                FILE_CONTENTS_SESSION_KEY,
                                servletContextFileContents);
            }
            servletContextFileContents.add(fileContent);
        }
    }

    /**
     * 保存用户上传的文件信息（未处理）
     * 
     * @param request
     * @param fileContent
     */
    public static void addFileContent(HttpServletRequest request,
                    FileContent fileContent) {
        if (request != null) {
            addFileContent(request.getSession(), fileContent);
        }
    }

    /**
     * 保存已移除的用户上传的文件信息（已处理）
     * 
     * @param session
     * @param fileContent
     */
    public static void addRemovedFileContent(HttpSession session,
                    FileContent fileContent) {
        if (session != null) {
            List<FileContent> removedFileContents = getRemovedSessionFileContents(session);
            if (removedFileContents == null) {
                removedFileContents = new ArrayList<FileContent>();
                session.setAttribute(FILE_CONTENTS_REMOVED_SESSION_KEY,
                                removedFileContents);
            }
            removedFileContents.add(fileContent);
        }
    }

    /**
     * 保存已移除的用户上传的文件信息（已处理）
     * 
     * @param request
     * @param fileContent
     */
    public static void addRemovedFileContent(HttpServletRequest request,
                    FileContent fileContent) {
        if (request != null) {
            addRemovedFileContent(request.getSession(), fileContent);
        }
    }

    /**
     * 保存已移除的用户上传的文件信息（已处理）
     * 
     * @param session
     * @param fileContents
     */
    public static void addRemovedFileContent(HttpSession session,
                    List<FileContent> fileContents) {
        if (session != null) {
            List<FileContent> removedFileContents = getRemovedSessionFileContents(session);
            if (removedFileContents == null) {
                removedFileContents = new ArrayList<FileContent>();
                session.setAttribute(FILE_CONTENTS_REMOVED_SESSION_KEY,
                                removedFileContents);
            }
            removedFileContents.addAll(fileContents);
        }
    }

    /**
     * 保存已移除的用户上传的文件信息（已处理）
     * 
     * @param request
     * @param fileContents
     */
    public static void addRemovedFileContent(HttpServletRequest request,
                    List<FileContent> fileContents) {
        if (request != null) {
            addRemovedFileContent(request.getSession(), fileContents);
        }
    }

    /**
     * 移除已保存的用户已上传文件信息
     * 
     * @param session
     * @param fileContent
     */
    public static void removeFileContent(HttpSession session,
                    FileContent fileContent) {
        if (session != null) {
            // 移除Session中保存的记录
            List<FileContent> sessionFileContents = getSessionFileContents(session);
            if (sessionFileContents != null) {
                sessionFileContents.remove(fileContent);
            }
            // 移除ServletContext中保存的记录
            List<FileContent> servletContextFileContents = getServletContextFileContents(session);
            if (servletContextFileContents != null) {
                servletContextFileContents.remove(fileContent);
            }
            // 保存移除记录
            addRemovedFileContent(session, fileContent);
        }
    }

    /**
     * 移除已保存的用户已上传文件信息
     * 
     * @param request
     * @param fileContent
     */
    public static void removeFileContent(HttpServletRequest request,
                    FileContent fileContent) {
        if (request != null) {
            removeFileContent(request.getSession(), fileContent);
        }
    }

    /**
     * 移除已保存的用户已上传文件信息
     * 
     * @param session
     * @param fileContents
     */
    public static void removeFileContent(HttpSession session,
                    List<FileContent> fileContents) {
        if (session != null) {
            // 移除Session中保存的记录
            List<FileContent> sessionFileContents = getSessionFileContents(session);
            if (sessionFileContents != null) {
                sessionFileContents.removeAll(fileContents);
            }
            // 移除ServletContext中保存的记录
            List<FileContent> servletContextFileContents = getServletContextFileContents(session);
            if (servletContextFileContents != null) {
                servletContextFileContents.removeAll(fileContents);
            }
            // 保存移除记录
            addRemovedFileContent(session, fileContents);
        }
    }

    /**
     * 移除已保存的用户已上传文件信息
     * 
     * @param request
     * @param fileContents
     */
    public static void removeFileContent(HttpServletRequest request,
                    List<FileContent> fileContents) {
        if (request != null) {
            removeFileContent(request.getSession(), fileContents);
        }
    }

    /**
     * 清理当前用户上传的文件信息
     * 
     * @param session
     * @param fileContentService
     */
    public static void cleanFileContent(HttpSession session,
                    FileContentService fileContentService) {
        if (session != null) {
            List<FileContent> sessionFileContents = getSessionFileContents(session);
            // 移除ServletContext中的记录
            List<FileContent> servletContextFileContents = getServletContextFileContents(session);
            if (servletContextFileContents != null) {
                if (sessionFileContents != null) {
                    for (FileContent fileContent : sessionFileContents) {
                        servletContextFileContents.remove(fileContent);
                    }
                }
                List<FileContent> removedFileContents = getRemovedSessionFileContents(session);
                if (removedFileContents != null) {
                    for (FileContent fileContent : removedFileContents) {
                        servletContextFileContents.remove(fileContent);
                    }
                }
            }

            User user = AuthUtils.getCurrentUser(session);
            if (sessionFileContents != null) {
                ServletContext context = session.getServletContext();
                String savePath = context
                                .getRealPath(FileUploadAction.SAVE_PATH);
                File path = new File(savePath);

                for (FileContent fileContent : sessionFileContents) {
                    try {
                        // 删除数据库记录
                        fileContentService.deleteFileContent(user, fileContent);
                        // 删除文件
                        File file = new File(path, fileContent.getPath()
                                        + File.separator
                                        + fileContent.getName());
                        FileUtils.deleteFile(file);
                        logger.info("delete uploaded file {}",
                                        file.getAbsolutePath());
                    } catch (IOException e) {
                        logger.error(e.getMessage(), e);
                    }
                }
                // 清除Session中文件上传记录
                session.removeAttribute(FILE_CONTENTS_SESSION_KEY);
                session.removeAttribute(FILE_CONTENTS_REMOVED_SESSION_KEY);
            }
        }
    }

    /**
     * 清理当前用户上传的文件信息
     * 
     * @param request
     * @param fileContentService
     */
    public static void cleanFileContent(HttpServletRequest request,
                    FileContentService fileContentService) {
        if (request != null) {
            cleanFileContent(request.getSession(), fileContentService);
        }
    }

    /**
     * 删除已上传的文件
     * 
     * @param fileContent
     * @param request
     */
    public static void deleteFileContent(FileContent fileContent,
                    HttpServletRequest request) {
        if (request != null) {
            deleteFileContent(fileContent, request.getSession());
        }
    }

    /**
     * 删除已上传的文件
     * 
     * @param fileContent
     * @param session
     */
    public static void deleteFileContent(FileContent fileContent,
                    HttpSession session) {
        if (session == null) {
            return;
        }

        ServletContext context = session.getServletContext();
        String savePath = context.getRealPath(FileUploadAction.SAVE_PATH);
        File path = new File(savePath);
        File file = new File(path, fileContent.getPath() + File.separator
                        + fileContent.getName());
        try {
            FileUtils.deleteFile(file);
            logger.info("delete uploaded file {}", file.getAbsolutePath());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

}
