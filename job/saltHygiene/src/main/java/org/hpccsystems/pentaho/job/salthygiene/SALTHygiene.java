/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hpccsystems.pentaho.job.salthygiene;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hpccsystems.eclguifeatures.AutoPopulate;
import org.hpccsystems.recordlayout.RecordBO;
import org.hpccsystems.recordlayout.RecordList;
import org.hpccsystems.saltui.EntryBO;
import org.hpccsystems.saltui.EntryList;
import org.hpccsystems.saltui.HygieneRuleBO;
import org.hpccsystems.saltui.HygieneRuleList;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.compatibility.Value;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryBase;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.w3c.dom.Node;
import org.hpccsystems.ecljobentrybase.*;
import org.hpccsystems.javaecl.SaltHygieneReport;
/**
 *
 * @author ChambersJ
 */
public class SALTHygiene extends ECLJobEntry{//extends JobEntryBase implements Cloneable, JobEntryInterface {
    
	
    private String datasetName;
    private String layout;
	//private String rules;
	private EntryList entryList = new EntryList();
	private HygieneRuleList fieldTypeList = new HygieneRuleList();
   
	public String getDatasetName() {
		return datasetName;
	}
	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
	}
	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
    public HygieneRuleList getFieldTypeList() {
		return fieldTypeList;
	}
	public void setFieldTypeList(HygieneRuleList fieldTypeList) {
		this.fieldTypeList = fieldTypeList;
	}
	public EntryList getEntryList() {
		return entryList;
	}
	public void setEntryList(EntryList entryList) {
		this.entryList = entryList;
	}
	
	
	
	public String saveEntryList(){
        String out = "";
        ArrayList<EntryBO> list = entryList.getEntries();
        Iterator<EntryBO> itr = list.iterator();
        boolean isFirst = true;
        while(itr.hasNext()){
            if(!isFirst){out+="|";}
            
            out += itr.next().toCSV();
            isFirst = false;
        }
        
        //fieldTypes
        return out;
    }
    
    public void openEntryList(String in){
        String[] strLine = in.split("[|]");
        int len = strLine.length;
        if(len>0){
            entryList = new EntryList();
            for(int i =0; i<len; i++){
                EntryBO eb = new EntryBO(strLine[i]);
                entryList.addEntryBO(eb);
            }
        }
        
        //fieldTypes
    }
    
    public String saveFieldTypeList(){
        String out = "";
        ArrayList<HygieneRuleBO> list = fieldTypeList.getFields();
        Iterator<HygieneRuleBO> itr = list.iterator();
        boolean isFirst = true;
        while(itr.hasNext()){
            if(!isFirst){out+="|";}
            
            out += itr.next().toCSV();
            isFirst = false;
        }
        
        //fieldTypes
        return out;
    }
    
    public void openFieldTypeList(String in){
        String[] strLine = in.split("[|]");
        int len = strLine.length;
        
        if(len>0){
            fieldTypeList = new HygieneRuleList();
            for(int i =0; i<len; i++){
                HygieneRuleBO ft = new HygieneRuleBO(strLine[i]);
                ft.fromCSV(strLine[i]);
                fieldTypeList.add(ft);
            }
        }
        
        //fieldTypes
    }
    
    
	@Override
    public Result execute(Result prevResult, int k) throws KettleException {
        
        Result result = prevResult;
        
        AutoPopulate ap = new AutoPopulate();
        String jobNameNoSpace = "";
        JobMeta jobMeta = super.parentJob.getJobMeta();
        try{
                String jobName = ap.getGlobalVariable(jobMeta.getJobCopies(),"jobName");
                jobNameNoSpace = jobName.replace(" ", "_");  
                this.setLayout(ap.getDatasetsField("record_name", this.getDatasetName(),jobMeta.getJobCopies()));
                
            }catch (Exception e){
                System.out.println("Error Parsing existing Global Variables ");
                System.out.println(e.toString());
                e.printStackTrace();

            }
        
        SaltHygieneReport shr = new SaltHygieneReport();
        shr.setDatasetName(datasetName);
        shr.setName(this.getName());
        shr.setSaltLib(jobNameNoSpace + "_module");
        shr.setLayout(this.getLayout());
       
        logBasic("{Dataset Job} Execute = " + shr.ecl());
        logBasic("{Dataset Job} Previous =" + result.getLogText());
        
        result.setResult(true);
        
        RowMetaAndData data = new RowMetaAndData();
        data.addValue("ecl", Value.VALUE_TYPE_STRING, shr.ecl());
        
        List list = result.getRows();
        list.add(data);
        String eclCode = "";
        if (list == null) {
            list = new ArrayList();
        } else {
            
            for (int i = 0; i < list.size(); i++) {
                RowMetaAndData rowData = (RowMetaAndData) list.get(i);
                String code = rowData.getString("ecl", null);
                if (code != null) {
                    eclCode += code;
                }
            }
            logBasic("{Dataset Job} ECL Code =" + eclCode);
        }

        result.setRows(list);
        result.setLogText("ECLDataset executed, ECL code added");
        
        
        
        /*
        AutoPopulate ap = new AutoPopulate();
        String jobNameNoSpace = "";
        JobMeta jobMeta = super.parentJob.getJobMeta();
        
        
        try{
        //Object[] jec = this.jobMeta.getJobCopies().toArray();

            String jobName = ap.getGlobalVariable(jobMeta.getJobCopies(),"jobName");
            jobNameNoSpace = jobName.replace(" ", "_"); 
            
            this.setLayout(ap.getDatasetsField("record_name", this.getDatasetName(),jobMeta.getJobCopies()));
            
        }catch (Exception e){
            System.out.println("Error Parsing existing Global Variables ");
            System.out.println(e.toString());
            e.printStackTrace();

        }
        


       
        
        logBasic("{Dataset Job} Previous =" + result.getLogText());
        
        result.setResult(true);
        
        RowMetaAndData data = new RowMetaAndData();
        
        
        List list = result.getRows();
        list.add(data);
        String eclCode = "";
        if (list == null) {
            list = new ArrayList();
        } else {
            
            for (int i = 0; i < list.size(); i++) {
                RowMetaAndData rowData = (RowMetaAndData) list.get(i);
                String code = rowData.getString("ecl", null);
                if (code != null) {
                    eclCode += code;
                }
            }
            logBasic("{Dataset Job} ECL Code =" + eclCode);
        }
        
        result.setRows(list);
        result.setLogText("ECLDataset executed, ECL code added");
        */
        return result;
    }
 
    @Override
    public void loadXML(Node node, List<DatabaseMeta> list, List<SlaveServer> list1, Repository rpstr) throws KettleXMLException {
        try {
            super.loadXML(node, list, list1);
            
            if(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "datasetName")) != null)
            	setDatasetName(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "datasetName")));
            if(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "layout")) != null)
            	setLayout(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "layout")));
            if(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "entryList")) != null)
                openEntryList(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "entryList")));
            if(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "fieldTypeList")) != null)
                openFieldTypeList(XMLHandler.getNodeValue(XMLHandler.getSubNode(node, "fieldTypeList")));
            
        } catch (Exception e) {
            throw new KettleXMLException("ECL Dataset Job Plugin Unable to read step info from XML node", e);
        }

    }

    public String getXML() {
        String retval = "";
        
        retval += super.getXML();
      
        retval += "		<datasetName><![CDATA[" + datasetName + "]]></datasetName>" + Const.CR;
        retval += "		<layout><![CDATA[" + layout + "]]></layout>" + Const.CR;
        retval += "		<entryList><![CDATA[" + this.saveEntryList() + "]]></entryList>" + Const.CR;
        retval += "		<fieldTypeList><![CDATA[" + this.saveFieldTypeList() + "]]></fieldTypeList>" + Const.CR;
        return retval;

    }

    public void loadRep(Repository rep, ObjectId id_jobentry, List<DatabaseMeta> databases, List<SlaveServer> slaveServers)
            throws KettleException {
        try {
            if(rep.getStepAttributeString(id_jobentry, "datasetName") != null)
                datasetName = rep.getStepAttributeString(id_jobentry, "datasetName"); //$NON-NLS-1$
            if(rep.getStepAttributeString(id_jobentry, "layout") != null)
                layout = rep.getStepAttributeString(id_jobentry, "layout"); //$NON-NLS-1$
            
            if(rep.getStepAttributeString(id_jobentry, "entryList") != null)
                this.openEntryList(rep.getStepAttributeString(id_jobentry, "entryList")); //$NON-NLS-1$
            
            if(rep.getStepAttributeString(id_jobentry, "fieldTypeList") != null)
                this.openFieldTypeList(rep.getStepAttributeString(id_jobentry, "fieldTypeList")); //$NON-NLS-1$
        
        } catch (Exception e) {
            throw new KettleException("Unexpected Exception", e);
        }
    }

    public void saveRep(Repository rep, ObjectId id_job) throws KettleException {
        try {
            
            rep.saveStepAttribute(id_job, getObjectId(), "datasetName", datasetName); //$NON-NLS-1$
            rep.saveStepAttribute(id_job, getObjectId(), "layout", layout); //$NON-NLS-1$
            rep.saveStepAttribute(id_job, getObjectId(), "entryList", this.saveEntryList()); //$NON-NLS-1$.
            rep.saveStepAttribute(id_job, getObjectId(), "fieldTypeList", this.saveFieldTypeList()); //$NON-NLS-1$
        } catch (Exception e) {
            throw new KettleException("Unable to save info into repository" + id_job, e);
        }
    }

    public boolean evaluates() {
        return true;
    }

    public boolean isUnconditional() {
        return true;
    }
}
