package org.xhome.xfileupload.core.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.xhome.db.query.QueryBase;
import org.xhome.xauth.User;
import org.xhome.xfileupload.FileContent;

/**
 * @project xfileupload-core
 * @author jhat
 * @email cpf624@126.com
 * @homepage http://pfchen.org
 * @date Mar 14, 2014
 * @describe
 */
@Service
public interface FileContentService {

    public int addFileContent(User oper, FileContent fileContent);

    public int updateFileContent(User oper, FileContent fileContent);

    public int lockFileContent(User oper, FileContent fileContent);

    public int unlockFileContent(User oper, FileContent fileContent);

    public int deleteFileContent(User oper, FileContent fileContent);

    public int deleteFileContents(User oper, List<FileContent> fileContents);

    public boolean isFileContentUpdateable(User oper, FileContent fileContent);

    public boolean isFileContentLocked(User oper, FileContent fileContent);

    public boolean isFileContentDeleteable(User oper, FileContent fileContent);

    public FileContent getFileContent(User oper, long id);

    public List<FileContent> getFileContents(User oper, QueryBase query);

    public long countFileContents(User oper, QueryBase query);

}
