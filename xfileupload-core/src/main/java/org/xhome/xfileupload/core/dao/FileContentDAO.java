package org.xhome.xfileupload.core.dao;

import java.util.List;

import org.xhome.db.query.QueryBase;
import org.xhome.xfileupload.FileContent;

/**
 * @project xfileupload-core
 * @author jhat
 * @email cpf624@126.com
 * @homepage http://pfchen.org
 * @date Mar 14, 2014
 * @describe
 */
public interface FileContentDAO {

    public int addFileContent(FileContent fileContent);

    public int updateFileContent(FileContent fileContent);

    public int lockFileContent(FileContent fileContent);

    public int unlockFileContent(FileContent fileContent);

    public int deleteFileContent(FileContent fileContent);

    public boolean isFileContentUpdateable(FileContent fileContent);

    public boolean isFileContentLocked(FileContent fileContent);

    public boolean isFileContentDeleteable(FileContent fileContent);

    public FileContent queryFileContentById(Long id);

    public List<FileContent> queryFileContents(QueryBase query);

    public long countFileContents(QueryBase query);

}
