package org.xhome.xfileupload.core.dao;

import java.util.List;

import org.junit.Test;
import org.xhome.db.query.QueryBase;
import org.xhome.xfileupload.FileContent;
import org.xhome.xfileupload.core.AbstractTest;

/**
 * @project xfileupload-core
 * @author jhat
 * @email cpf624@126.com
 * @homepage http://pfchen.org
 * @date Mar 14, 2014
 * @describe
 */
public class FileContentDAOTest extends AbstractTest {

    private FileContentDAO fileContentDAO;

    public FileContentDAOTest() {
        fileContentDAO = context.getBean(FileContentDAO.class);
    }

    @Test
    public void testAddFileContent() {
        FileContent fileContent = new FileContent();
        fileContent.setPath("2014/03/13");
        fileContent.setName("abcdefg.jpg");
        fileContent.setOriginal("jhat.jpg");
        fileContent.setType("jpeg");
        fileContent.setSize(1024L);
        fileContent.setOwner(1L);
        fileContent.setModifier(1L);
        fileContentDAO.addFileContent(fileContent);
    }

    @Test
    public void testGetFileContent() {
        FileContent fileContent = fileContentDAO.queryFileContentById(1L);
        printFileContent(fileContent);
    }

    @Test
    public void testQueryFileContent() {
        QueryBase query = new QueryBase();
        List<FileContent> fileContents = fileContentDAO
                        .queryFileContents(query);
        printFileContent(fileContents);
    }

    @Test
    public void testUpdateFileContent() {
        FileContent fileContent = fileContentDAO.queryFileContentById(1L);
        printFileContent(fileContent);

        fileContentDAO.updateFileContent(fileContent);

        fileContent = fileContentDAO.queryFileContentById(1L);
        printFileContent(fileContent);
    }

    @Test
    public void testDeleteFileContent() {
        FileContent fileContent = fileContentDAO.queryFileContentById(1L);
        fileContentDAO.deleteFileContent(fileContent);
    }

}
