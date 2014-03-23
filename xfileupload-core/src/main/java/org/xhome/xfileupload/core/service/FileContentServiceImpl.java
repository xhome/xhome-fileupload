package org.xhome.xfileupload.core.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.xhome.common.constant.Action;
import org.xhome.common.constant.Status;
import org.xhome.db.query.QueryBase;
import org.xhome.xauth.ManageLog;
import org.xhome.xauth.User;
import org.xhome.xauth.core.service.ManageLogService;
import org.xhome.xfileupload.FileContent;
import org.xhome.xfileupload.ManageLogType;
import org.xhome.xfileupload.core.dao.FileContentDAO;
import org.xhome.xfileupload.core.listener.FileContentManageListener;

/**
 * @project xfileupload-core
 * @author jhat
 * @email cpf624@126.com
 * @date Sep 10, 201311:53:33 PM
 * @describe
 */
@Service
public class FileContentServiceImpl implements FileContentService {

    @Autowired
    private FileContentDAO                  fileContentDAO;
    @Autowired
    private ManageLogService                manageLogService;
    @Autowired(required = false)
    private List<FileContentManageListener> fileContentManageListeners;

    private Logger                          logger;

    public FileContentServiceImpl() {
        logger = LoggerFactory.getLogger(FileContentService.class);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    @Override
    public int addFileContent(User oper, FileContent fileContent) {
        String name = fileContent.getName();

        if (!this.beforeFileContentManage(oper, Action.ADD, fileContent)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to add fileContent {}, but it's blocked",
                                name);
            }

            this.logManage(name, Action.ADD, null, Status.BLOCKED, oper);
            this.afterFileContentManage(oper, Action.ADD, Status.BLOCKED,
                            fileContent);
            return Status.BLOCKED;
        }

        fileContent.setStatus(Status.OK);
        fileContent.setVersion((short) 0);
        Timestamp t = new Timestamp(System.currentTimeMillis());
        fileContent.setCreated(t);
        fileContent.setModified(t);

        short r = fileContentDAO.addFileContent(fileContent) == 1 ? Status.SUCCESS
                        : Status.ERROR;

        if (logger.isDebugEnabled()) {
            if (r == Status.SUCCESS) {
                logger.debug("success to add fileContent {}", name);
            } else {
                logger.debug("fail to add fileContent {}", name);
            }
        }

        this.logManage(name, Action.ADD, fileContent.getId(), r, oper);
        this.afterFileContentManage(oper, Action.ADD, r, fileContent);
        return r;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    @Override
    public int updateFileContent(User oper, FileContent fileContent) {
        String name = fileContent.getName();
        Long id = fileContent.getId();

        if (!this.beforeFileContentManage(oper, Action.UPDATE, fileContent)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to update fileContent {}[{}], but it's blocked",
                                name, id);
            }

            this.logManage(name, Action.UPDATE, null, Status.BLOCKED, oper);
            this.afterFileContentManage(oper, Action.UPDATE, Status.BLOCKED,
                            fileContent);
            return Status.BLOCKED;
        }

        FileContent old = fileContentDAO.queryFileContentById(id);

        if (old == null) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to update fileContent {}[{}], but it's not exists",
                                name, id);
            }

            this.logManage(name, Action.UPDATE, id, Status.NOT_EXISTS, oper);
            this.afterFileContentManage(oper, Action.UPDATE, Status.NOT_EXISTS,
                            fileContent);
            return Status.NOT_EXISTS;
        }
        fileContent.setOwner(old.getOwner());
        fileContent.setCreated(old.getCreated());

        String oldName = old.getName();

        if (!old.getVersion().equals(fileContent.getVersion())) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to update fileContent {}[{}], but version not match",
                                oldName, id);
            }

            this.logManage(oldName, Action.UPDATE, id,
                            Status.VERSION_NOT_MATCH, oper);
            this.afterFileContentManage(oper, Action.UPDATE,
                            Status.VERSION_NOT_MATCH, fileContent);
            return Status.VERSION_NOT_MATCH;
        }

        short status = old.getStatus();
        if (status == Status.NO_UPDATE || status == Status.LOCK) {
            if (logger.isDebugEnabled()) {
                logger.debug("it's not allowed to update fileContent {}[{}]",
                                oldName, id);
            }

            this.logManage(oldName, Action.UPDATE, id, status, oper);
            this.afterFileContentManage(oper, Action.UPDATE, Status.EXISTS,
                            fileContent);
            return status;
        }

        Timestamp t = new Timestamp(System.currentTimeMillis());
        fileContent.setModified(t);

        short r = fileContentDAO.updateFileContent(fileContent) == 1 ? Status.SUCCESS
                        : Status.ERROR;
        if (r == Status.SUCCESS) {
            fileContent.incrementVersion();
        }

        if (logger.isDebugEnabled()) {
            if (r == Status.SUCCESS) {
                logger.debug("success to update fileContent {}[{}]", oldName,
                                id);
            } else {
                logger.debug("fail to update fileContent {}[{}]", oldName, id);
            }
        }

        this.logManage(oldName, Action.UPDATE, id, r, oper);
        this.afterFileContentManage(oper, Action.UPDATE, r, fileContent);
        return r;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    @Override
    public int lockFileContent(User oper, FileContent fileContent) {
        String name = fileContent.getName();
        Long id = fileContent.getId();

        if (!this.beforeFileContentManage(oper, Action.LOCK, fileContent)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to lock fileContent {}[{}], but it's blocked",
                                name, id);
            }

            this.logManage(name, Action.LOCK, null, Status.BLOCKED, oper);
            this.afterFileContentManage(oper, Action.LOCK, Status.BLOCKED,
                            fileContent);
            return Status.BLOCKED;
        }

        short r = fileContentDAO.lockFileContent(fileContent) == 1 ? Status.SUCCESS
                        : Status.ERROR;

        if (logger.isDebugEnabled()) {
            if (r == Status.SUCCESS) {
                logger.debug("success to lock fileContent {}[{}]", name, id);
            } else {
                logger.debug("fail to lock fileContent {}[{}]", name, id);
            }
        }

        this.logManage(name, Action.LOCK, id, r, oper);
        this.afterFileContentManage(oper, Action.LOCK, r, fileContent);
        return r;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    @Override
    public int unlockFileContent(User oper, FileContent fileContent) {
        String name = fileContent.getName();
        Long id = fileContent.getId();

        if (!this.beforeFileContentManage(oper, Action.UNLOCK, fileContent)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to unlock fileContent {}[{}], but it's blocked",
                                name, id);
            }

            this.logManage(name, Action.UNLOCK, null, Status.BLOCKED, oper);
            this.afterFileContentManage(oper, Action.UNLOCK, Status.BLOCKED,
                            fileContent);
            return Status.BLOCKED;
        }

        short r = fileContentDAO.unlockFileContent(fileContent) == 1 ? Status.SUCCESS
                        : Status.ERROR;

        if (logger.isDebugEnabled()) {
            if (r == Status.SUCCESS) {
                logger.debug("success to unlock fileContent {}[{}]", name, id);
            } else {
                logger.debug("fail to unlock fileContent {}[{}]", name, id);
            }
        }

        this.logManage(name, Action.UNLOCK, id, r, oper);
        this.afterFileContentManage(oper, Action.UNLOCK, r, fileContent);
        return r;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    @Override
    public int deleteFileContent(User oper, FileContent fileContent) {
        String name = fileContent.getName();
        Long id = fileContent.getId();

        if (!this.beforeFileContentManage(oper, Action.DELETE, fileContent)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to delete fileContent {}[{}], but it's blocked",
                                name, id);
            }

            this.logManage(name, Action.DELETE, null, Status.BLOCKED, oper);
            this.afterFileContentManage(oper, Action.DELETE, Status.BLOCKED,
                            fileContent);
            return Status.BLOCKED;
        }

        short r = Status.SUCCESS;
        if (fileContentDAO.isFileContentDeleteable(fileContent)) {
            if (logger.isDebugEnabled()) {
                logger.debug("delete fileContent {}[{}]", name, id);
            }
            fileContentDAO.deleteFileContent(fileContent);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("fileContent {}[{}] isn't deleteable", name, id);
            }
            r = Status.NO_DELETE;
        }

        this.logManage(name, Action.DELETE, id, r, oper);
        this.afterFileContentManage(oper, Action.DELETE, r, fileContent);
        return r;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Throwable.class)
    @Override
    public int deleteFileContents(User oper, List<FileContent> fileContents) {
        int r = Status.SUCCESS;
        for (FileContent fileContent : fileContents) {
            r = this.deleteFileContent(oper, fileContent);
            if (r != Status.SUCCESS) {
                throw new RuntimeException("fail to delete fileContent ["
                                + fileContent.getId() + "]"
                                + fileContent.getName());
            }
        }
        return r;
    }

    @Override
    public boolean isFileContentUpdateable(User oper, FileContent fileContent) {
        String name = fileContent.getName();
        Long id = fileContent.getId();

        if (!this.beforeFileContentManage(oper, Action.IS_UPDATEABLE,
                        fileContent)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to juge updateable of fileContent {}[{}], but it's blocked",
                                name, id);
            }

            this.logManage(name, Action.IS_UPDATEABLE, null, Status.BLOCKED,
                            oper);
            this.afterFileContentManage(oper, Action.IS_UPDATEABLE,
                            Status.BLOCKED, fileContent);
            return false;
        }

        boolean e = fileContentDAO.isFileContentUpdateable(fileContent);

        if (logger.isDebugEnabled()) {
            if (e) {
                logger.debug("fileContent {}[{}] is updateable", name, id);
            } else {
                logger.debug("fileContent {}[{}] isn't updateable", name, id);
            }
        }

        this.logManage(name, Action.IS_UPDATEABLE, id, Status.SUCCESS, oper);
        this.afterFileContentManage(oper, Action.IS_UPDATEABLE, Status.SUCCESS,
                        fileContent);
        return e;
    }

    @Override
    public boolean isFileContentLocked(User oper, FileContent fileContent) {
        String name = fileContent.getName();
        Long id = fileContent.getId();

        if (!this.beforeFileContentManage(oper, Action.IS_LOCKED, fileContent)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to juge locked of fileContent {}[{}], but it's blocked",
                                name, id);
            }

            this.logManage(name, Action.IS_LOCKED, null, Status.BLOCKED, oper);
            this.afterFileContentManage(oper, Action.IS_LOCKED, Status.BLOCKED,
                            fileContent);
            return false;
        }

        boolean e = fileContentDAO.isFileContentLocked(fileContent);

        if (logger.isDebugEnabled()) {
            if (e) {
                logger.debug("fileContent {}[{}] is locked", name, id);
            } else {
                logger.debug("fileContent {}[{}] isn't locked", name, id);
            }
        }

        this.logManage(name, Action.IS_LOCKED, id, Status.SUCCESS, oper);
        this.afterFileContentManage(oper, Action.IS_LOCKED, Status.SUCCESS,
                        fileContent);
        return e;
    }

    @Override
    public boolean isFileContentDeleteable(User oper, FileContent fileContent) {
        String name = fileContent.getName();
        Long id = fileContent.getId();

        if (!this.beforeFileContentManage(oper, Action.IS_DELETEABLE,
                        fileContent)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to juge deleteable of fileContent {}[{}], but it's blocked",
                                name, id);
            }

            this.logManage(name, Action.IS_DELETEABLE, null, Status.BLOCKED,
                            oper);
            this.afterFileContentManage(oper, Action.IS_DELETEABLE,
                            Status.BLOCKED, fileContent);
            return false;
        }

        boolean e = fileContentDAO.isFileContentDeleteable(fileContent);

        if (logger.isDebugEnabled()) {
            if (e) {
                logger.debug("fileContent {}[{}] is deleteable", name, id);
            } else {
                logger.debug("fileContent {}[{}] isn't deleteable", name, id);
            }
        }

        this.logManage(name, Action.IS_DELETEABLE, id, Status.SUCCESS, oper);
        this.afterFileContentManage(oper, Action.IS_DELETEABLE, Status.SUCCESS,
                        fileContent);
        return e;
    }

    @Override
    public FileContent getFileContent(User oper, long id) {
        if (!this.beforeFileContentManage(oper, Action.GET, null, id)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to get fileContent of id {}, but it's blocked",
                                id);
            }

            this.logManage("" + id, Action.GET, null, Status.BLOCKED, oper);
            this.afterFileContentManage(oper, Action.GET, Status.BLOCKED, null,
                            id);
            return null;
        }

        FileContent fileContent = fileContentDAO.queryFileContentById(id);

        String name = null;
        if (logger.isDebugEnabled()) {
            if (fileContent != null) {
                name = fileContent.getName();
                logger.debug("get fileContent {}[{}]", name, id);
            } else {
                logger.debug("fileContent of id {} is not exists", id);
            }
        }

        this.logManage(name, Action.GET, id, Status.SUCCESS, oper);
        this.afterFileContentManage(oper, Action.GET, Status.SUCCESS,
                        fileContent, id);
        return fileContent;
    }

    @Override
    public List<FileContent> getFileContents(User oper, QueryBase query) {
        if (!this.beforeFileContentManage(oper, Action.QUERY, null, query)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to query fileContents, but it's blocked");
            }

            this.logManage(null, Action.QUERY, null, Status.BLOCKED, oper);
            this.afterFileContentManage(oper, Action.QUERY, Status.BLOCKED,
                            null, query);
            return null;
        }

        List<FileContent> results = fileContentDAO.queryFileContents(query);
        if (query != null) {
            query.setResults(results);
            long total = fileContentDAO.countFileContents(query);
            query.setTotal(total);
        }

        if (logger.isDebugEnabled()) {
            if (query != null) {
                logger.debug("query fileContents with parameters {}",
                                query.getParameters());
            } else {
                logger.debug("query fileContents");
            }
        }

        this.logManage(null, Action.QUERY, null, Status.SUCCESS, oper);
        this.afterFileContentManage(oper, Action.QUERY, Status.SUCCESS, null,
                        query);
        return results;
    }

    @Override
    public long countFileContents(User oper, QueryBase query) {
        if (!this.beforeFileContentManage(oper, Action.COUNT, null, query)) {
            if (logger.isDebugEnabled()) {
                logger.debug("try to count fileContents, but it's blocked");
            }

            this.logManage(null, Action.COUNT, null, Status.BLOCKED, oper);
            this.afterFileContentManage(oper, Action.COUNT, Status.BLOCKED,
                            null, query);
            return -1;
        }

        long c = fileContentDAO.countFileContents(query);
        if (logger.isDebugEnabled()) {
            if (query != null) {
                logger.debug("count fileContents with parameters {} of {}",
                                query.getParameters(), c);
            } else {
                logger.debug("count fileContents of {}", c);
            }
        }

        this.logManage(null, Action.COUNT, null, Status.SUCCESS, oper);
        this.afterFileContentManage(oper, Action.COUNT, Status.SUCCESS, null,
                        query);
        return c;
    }

    private void logManage(String content, Short action, Long obj,
                    Short status, User oper) {
        ManageLog manageLog = new ManageLog(ManageLog.MANAGE_LOG_XFILEUPLOAD,
                        content, action, ManageLogType.FILECONTENT, obj,
                        oper == null ? null : oper.getId());
        manageLog.setStatus(status);
        manageLogService.logManage(manageLog);
    }

    private boolean beforeFileContentManage(User oper, short action,
                    FileContent fileContent, Object... args) {
        if (fileContentManageListeners != null) {
            for (FileContentManageListener listener : fileContentManageListeners) {
                if (!listener.beforeFileContentManage(oper, action,
                                fileContent, args)) {
                    return false;
                }
            }
        }
        return true;
    }

    private void afterFileContentManage(User oper, short action, short result,
                    FileContent fileContent, Object... args) {
        if (fileContentManageListeners != null) {
            for (FileContentManageListener listener : fileContentManageListeners) {
                listener.afterFileContentManage(oper, action, result,
                                fileContent, args);
            }
        }
    }

    public void setFileContentDAO(FileContentDAO fileContentDAO) {
        this.fileContentDAO = fileContentDAO;
    }

    public FileContentDAO getFileContentDAO() {
        return this.fileContentDAO;
    }

    public void setManageLogService(ManageLogService manageLogService) {
        this.manageLogService = manageLogService;
    }

    public ManageLogService getManageLogService() {
        return this.manageLogService;
    }

    public void setFileContentManageListeners(
                    List<FileContentManageListener> fileContentManageListeners) {
        this.fileContentManageListeners = fileContentManageListeners;
    }

    public List<FileContentManageListener> getFileContentManageListeners() {
        return fileContentManageListeners;
    }

    public void registerFileContentManageListener(
                    FileContentManageListener fileContentManageListener) {
        if (fileContentManageListeners == null) {
            fileContentManageListeners = new ArrayList<FileContentManageListener>();
        }
        fileContentManageListeners.add(fileContentManageListener);
    }

}
