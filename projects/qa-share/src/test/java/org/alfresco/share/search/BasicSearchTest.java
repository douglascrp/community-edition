package org.alfresco.share.search;

import org.alfresco.share.util.AbstractUtils;
import org.alfresco.share.util.ShareUser;
import org.alfresco.share.util.ShareUserSearchPage;
import org.alfresco.share.util.api.CreateUserAPI;
import org.alfresco.test.FailedTestListener;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class BasicSearchTest extends AbstractUtils
{
    private static Log logger = LogFactory.getLog(BasicSearchTest.class);

    protected String testUser;

    protected String siteName = "";

    /**
     * Class includes: Tests from TestLink in Area: Advanced Search Tests
     * <ul>
     * <li>Test searches using various Properties, content, Proximity, Range Queries</li>
     * </ul>
     */
    @Override
    @BeforeClass(alwaysRun = true)
    public void setup() throws Exception
    {
        super.setup();
        testName = this.getClass().getSimpleName();
        testUser = testName + "@" + DOMAIN_FREE;
        logger.info("Starting Tests: " + testName);
    }

    protected void basicSearch(WebDrone drone, Boolean isSiteDashboard, String searchType, String searchTerm, String entryToBeFound, Boolean isEntryVisible)
    {
        ShareUserSearchPage.basicSearch(drone, searchTerm, isSiteDashboard);

        // Check the Search Results
        Boolean searchOk = ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, searchType, searchTerm, entryToBeFound, isEntryVisible);

        if (!searchOk)
        {
            drone.refresh();
            drone.getCurrentPage().render();

            ShareUserSearchPage.basicSearch(drone, testName, false);
        }

    }

    // AdvancedSearchTest
    @Test(groups = { "DataPrepSearch" }, timeOut = 900000)
    public void dataPrep_AdvSearch_AONE_13015() throws Exception
    {
        String testName = getTestName().replace("-", "");
        String testUser = getUserNameFreeDomain(testName);
        String[] testUserInfo = new String[] { testUser };

        String siteName = getSiteName(testName);

        // Files
        String[] fileName = new String[21];
        fileName[0] = getFileName(testName + "." + "xlsx");
        fileName[1] = getFileName(testName + "." + "txt");
        fileName[2] = getFileName(testName + "." + "msg");
        fileName[3] = getFileName(testName + "." + "pdf");
        fileName[4] = getFileName(testName + "." + "xml");
        fileName[5] = getFileName(testName + "." + "html");
        fileName[6] = getFileName(testName + "." + "eml");
        fileName[7] = getFileName(testName + "." + "opd");
        fileName[8] = getFileName(testName + "." + "ods");
        fileName[9] = getFileName(testName + "." + "odt");
        fileName[10] = getFileName(testName + "." + "xls");
        fileName[11] = getFileName(testName + "." + "xsl");
        fileName[12] = getFileName(testName + "." + "doc");
        fileName[13] = getFileName(testName + "." + "docx");
        fileName[14] = getFileName(testName + "." + "pptx");
        fileName[15] = getFileName(testName + "." + "pot");
        fileName[16] = getFileName(testName + "." + "xsd");
        fileName[17] = getFileName(testName + "." + "js");
        fileName[18] = getFileName(testName + "." + "java");
        fileName[19] = getFileName(testName + "." + "css");
        fileName[20] = getFileName(testName + "." + "rtf");

        Integer fileTypes = fileName.length - 1;

        try
        {
            // User
            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // UpLoad Files
            for (int index = 0; index <= fileTypes; index++)
            {
                String[] fileInfo = { fileName[index] };
                ShareUser.uploadFileInFolder(drone, fileInfo);
            }
        }
        catch (Exception e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            ShareUser.logout(drone);
        }
    }

    /**
     * Test:
     * <ul>
     * <li>Login</li>
     * <li>Check Search Results for diff types of files: Search based on Content</li>
     * </ul>
     */
    @Test (groups = { "CloudOnly" }, timeOut = 900000, enabled = false) //duplicate test (the test is executed via WebDriver project))
    public void AONE_13015()
    {

        /** Start Test */
        String testName = getTestName().replace("-", "");

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] fileName = new String[21];

        fileName[0] = getFileName(testName + "." + "xlsx");
        fileName[1] = getFileName(testName + "." + "xml");
        fileName[2] = getFileName(testName + "." + "msg");
        fileName[3] = getFileName(testName + "." + "pdf");
        fileName[4] = getFileName(testName + "." + "xml");
        fileName[5] = getFileName(testName + "." + "html");
        fileName[6] = getFileName(testName + "." + "eml");
        fileName[7] = getFileName(testName + "." + "opd");
        fileName[8] = getFileName(testName + "." + "ods");
        fileName[9] = getFileName(testName + "." + "odt");
        fileName[10] = getFileName(testName + "." + "xls");
        fileName[11] = getFileName(testName + "." + "xsl");
        fileName[12] = getFileName(testName + "." + "doc");
        fileName[13] = getFileName(testName + "." + "docx");
        fileName[14] = getFileName(testName + "." + "pptx");
        fileName[15] = getFileName(testName + "." + "pot");
        fileName[16] = getFileName(testName + "." + "xsd");
        fileName[17] = getFileName(testName + "." + "js");
        fileName[18] = getFileName(testName + "." + "java");
        fileName[19] = getFileName(testName + "." + "css");
        fileName[20] = getFileName(testName + "." + "rtf");

        Integer fileTypes = fileName.length - 1;

        try
        {
            /** Test Steps */
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Search Specific Site
            // Open Site DashBoard
            ShareUser.openSiteDashboard(drone, siteName);

            // Search
            basicSearch(drone, false, BASIC_SEARCH, testName, fileName[fileTypes], true);

            // Check the Search Results
            Boolean searchOk = ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, BASIC_SEARCH, testName, fileName[fileTypes], true);
            Assert.assertTrue(searchOk, "Search Results don't include the last file: " + fileName[fileTypes]);

            // Check each result contains the search term: apart from xlsx
            for (int index = 1; index <= fileTypes; index++)
                if (!(fileName[index].endsWith("ods") || fileName[index].endsWith("odt")))
                    Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, fileName[index]), "Not Found " + fileName[index]);

            // Search all sites
            basicSearch(drone, true, BASIC_SEARCH, testName, fileName[fileTypes], true);

            // Check the Search Results
            searchOk = ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, BASIC_SEARCH, testName, fileName[fileTypes], true);
            Assert.assertTrue((searchOk), "Search Results don't include the last file: " + fileName[fileTypes]);

            // Check each result contains the search term: apart from xlsx
            for (int index = 1; index <= fileTypes; index++)
                if (!(fileName[index].endsWith("ods") || fileName[index].endsWith("odt")))
                    Assert.assertTrue(ShareUserSearchPage.isSearchItemInFacetSearchPage(drone, fileName[index]), "Not Found " + fileName[index]);

        }
        catch (Exception e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            ShareUser.logout(drone);
        }
    }

    @Test(groups = { "DataPrepSearch" }, timeOut = 900000)
    public void dataPrep_AdvSearch_AONE_13033() throws Exception
    {
        String testName = getTestName();
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String[] testUserInfo = new String[] { testUser };

        String fileName = getFileName(testName + ".txt");
        String folderName = getFolderName(testName);

        try
        {
            // User

            CreateUserAPI.CreateActivateUser(drone, ADMIN_USERNAME, testUserInfo);

            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Site
            ShareUser.createSite(drone, siteName, AbstractUtils.SITE_VISIBILITY_PUBLIC);

            // UpLoad Files
            String[] fileInfo = { fileName };
            ShareUser.uploadFileInFolder(drone, fileInfo);
            ShareUser.createFolderInFolder(drone, folderName, folderName, DOCLIB);
        }
        catch (Exception e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            ShareUser.logout(drone);
        }

    }

    /**
     * Class includes: Tests from TestLink in Area: Dash-board Tests
     * <ul>
     * <li>Login</li>
     * <li>Create Site: Public</li>
     * <li>Open User Dash-board</li>
     * <li>Check that the User Dash-board > My Sites Dashlet shows the new Site</li>
     * </ul>
     */
    @Test(groups = { "CloudOnly" }, timeOut = 900000, enabled = false) //duplicate test (the test is executed via WebDriver project))
    public void AONE_13033()
    {
        /** Start Test */
        String testName = getTestName();

        /** Test Data Setup */
        String testUser = getUserNameFreeDomain(testName);
        String siteName = getSiteName(testName);
        String fileName = getFileName(testName + ".txt");

        String[] searchTerm = new String[4];
        searchTerm[0] = "ISUNSET:'cm:" + "creator'";
        searchTerm[1] = "ISUNSET:'cm:" + "title'";
        searchTerm[2] = "ISUNSET:'cm:" + "description'";
        searchTerm[3] = "ISUNSET:'cm:" + "author'";

        Integer searchCount = searchTerm.length - 1;
        Boolean searchOk;

        try {
            /** Test Steps */
            // Login
            ShareUser.login(drone, testUser, DEFAULT_PASSWORD);

            // Open SiteDashBoard
            ShareUser.openSiteDashboard(drone, siteName);

            // Perform Basic Search
            ShareUserSearchPage.basicSearch(drone, searchTerm[0], false);

            // Check the Results
            basicSearch(drone, false, BASIC_SEARCH, searchTerm[0], fileName, false);
            searchOk = ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, BASIC_SEARCH, searchTerm[0], fileName, false);

            Assert.assertTrue(searchOk, "Incorrect Result for search term: " + searchTerm[0]);

            for (int index = 1; index <= searchCount; index++)
            {
                // Search
                basicSearch(drone, false, BASIC_SEARCH, searchTerm[index], fileName, true);

                searchOk = ShareUserSearchPage.checkFacetedSearchResultsWithRetry(drone, BASIC_SEARCH, searchTerm[index], fileName, true);
                Assert.assertTrue(searchOk, "Incorrect Result for search term: " + searchTerm[index] + " . Issue: ACE-3115");
            }
        }
        catch (Exception e)
        {
            reportError(drone, testName, e);
        }
        finally
        {
            ShareUser.logout(drone);
        }
    }
}
