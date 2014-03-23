package org.xhome.xfileupload.core.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhome.xauth.User;
import org.xhome.xfileupload.FileContent;

/**
 * @project xfileupload-core
 * @author jhat
 * @email cpf624@126.com
 * @date Sep 10, 201311:58:52 PM
 * @describe
 */
public class TestFileContentManageListener implements FileContentManageListener {

    private Logger logger = LoggerFactory.getLogger(TestFileContentManageListener.class);

    @Override
    public boolean beforeFileContentManage(User oper, short action,
                    FileContent fileContent, Object... args) {
        logger.debug("TEST BEFORE FILECONTENT MANAGE LISTENER {} {} {}", oper
                        .getName(), action,
                        fileContent != null ? fileContent.getName() : "NULL");
        return true;
    }

    @Override
    public void afterFileContentManage(User oper, short action, short result,
                    FileContent fileContent, Object... args) {
        logger.debug("TEST AFTER FILECONTENT MANAGE LISTENER {} {} {}", oper
                        .getName(), action,
                        fileContent != null ? fileContent.getName() : "NULL");
    }

}
