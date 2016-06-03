/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elle.ProjectManager.logic;

import com.elle.ProjectManager.entities.Issue;
import com.elle.ProjectManager.presentation.ProjectManagerWindow;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTable;

/**
 *
 * @author Yi
 * It is the class to manage all offline issues
 */
public class offlineIssueManager {
    private final ProjectManagerWindow projectManager;
    private final File dir;
    private Map<Issue, File> issuesList;
    private final String userName;
    
    //the id is for starting id of new issue, when offlineIssueManager instantiated, it has to set the number 
    //based on the current offline data folder
    
    private static int newIssueId;
    
    public offlineIssueManager(String userName) {
        //initialize the offline data folder
        projectManager = ProjectManagerWindow.getInstance();
        dir = FilePathFormat.localDataFilePath();
        issuesList = new HashMap();
        readInLocalData();
        newIssueId = initId();
        
        this.userName = userName;
    }
    
    private void readInLocalData(){
        File[] listOfFiles = dir.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            Issue temp = readIssueFromFile(listOfFiles[i]);
            if (temp != null) {
                issuesList.put(temp, listOfFiles[i]);
            }
        }
    }
    
    private Issue readIssueFromFile(File file) {
         try{
            ObjectInputStream ois =
                    new ObjectInputStream(new FileInputStream(file));
            Issue temp = (Issue)ois.readObject();
            ois.close();
            return temp;
          
        }  catch (IOException ex) {
            LoggingAspect.afterThrown(ex);
        } catch (ClassNotFoundException ex) {
            LoggingAspect.afterThrown(ex);
        }
        return null;
        
    }
    
    //populate the offline data
    public void loadTableData(JTable table) {
        for(Issue issue : issuesList.keySet()) {
            String app = issue.getApp();
            
            if (table.getName().equals(app))
                projectManager.insertTableRow(table, issue);    
            
        }
        
    }
    
    //look into data folder, and find the minimum number in current offline data folder
    private int initId() {
        File[] listOfFiles = dir.listFiles();
        int temp = -10;
        
        //get the minimum id from offline files
        for (int i = 0; i < listOfFiles.length; i++) {
            String name = listOfFiles[i].getName();
            Pattern p = Pattern.compile("_id_([-]?[0-9]+)");
            Matcher m = p.matcher(name);
            if (m.find()){
                int id = Integer.parseInt(m.group(1));
                if (id < temp) 
                    temp = id;   
            }
        } 
        return temp -1 ;  
    }
    
    //generate the filename
    private File generateFileName(Issue issue) {
        String status;
        String timeStamp = issue.getDatetimeLastMod().replaceAll(":", "-");
        
        if (issue.getId() < 0) status = "new";
        else status = "update";
        String filename = status + "_"+ userName + "_" + "id_" + issue.getId() + "_" + timeStamp + ".ser";
        return new File(dir, filename);
    }
    
    //save issue to file
    private boolean saveIssueToFile(Issue issue, File issuefile) {
      
        try {
            if (!issuefile.exists()) 
                issuefile.createNewFile();
	    FileOutputStream fos = new FileOutputStream(issuefile);	
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(issue);
            oos.close();
            LoggingAspect.addLogMsgWthDate("Offline issue : " + issue.getId() +" is saved successfully");
            return true;
            
        } catch (IOException ex) {
            LoggingAspect.afterThrown(ex);
            return false;
        } 
    }
    
    public boolean addIssue(Issue issue) { 
        if (issue.getId() == -1) {
            issue.setId(newIssueId);
            newIssueId --;  //need to update the newIssueId to use for next new issue
        }
        
        File filename = generateFileName(issue);
        
        if (saveIssueToFile(issue, filename)) {
            issuesList.put(issue, filename);
            
            return true;
        }
        else return false;
    }
    
    public boolean updateIssue(Issue issue) {
        
        Issue foundIssue = getIssue(issue);
        if (foundIssue == null) return addIssue(issue);
        else {
            removeIssue(foundIssue);
            return addIssue(issue);
        }
        
        
        
    }
    
    
    public void removeIssue(Issue issue) { 
        //clean up local data
        File filename = issuesList.get(issue);
        filename.delete();
        issuesList.remove(issue);
    }
    
    
    public void deleteIssues(int[] ids) {
        for (int i : ids) {
            for (Issue temp : issuesList.keySet()) {
                if (temp.getId()== i) {
                    removeIssue(temp);
                    break;
                }
            }
        } 
    }

    public Map<Issue, File> getIssuesList() {
        return issuesList;
    }
    
    
    public Issue getIssue(Issue issue) {
        for (Issue temp : issuesList.keySet()) 
            if (temp.getId() == issue.getId()) {
                return temp;
            }
        
        return null;
            
    }
    
    public Issue getIssue(int id) {
        for (Issue temp : issuesList.keySet()) 
            if (temp.getId() == id) {
                return temp;
            }   
        return null;
            
    }

}
