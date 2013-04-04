package com.hpccsystems.ui.concepts;

import java.util.Iterator;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.hpccsystems.ui.concepts.table.ConceptsRecord;
import com.hpccsystems.ui.concepts.table.ConceptsTable;
import com.hpccsystems.ui.constants.Constants;

public class ConfigureConceptsUI {
	
	private Text textReOrderType = null;
	private Text textConceptName = null;
	private Combo comboEffectOnSpecificity = null;
	private Text textThreshold = null;
	private Combo comboSegmentType = null;
	private Combo comboScale = null;
	private Text textSpecificity = null;
	private Text textSwitchValue =null;
	
	public void addChildControls(Shell shell){
		Composite compositeForConcept = new Composite(shell, SWT.NONE);
		GridLayout layout = new GridLayout();
	    layout.numColumns = 3;
	    layout.makeColumnsEqualWidth = true;
	    compositeForConcept.setLayout(layout);
	    
	    GridData data = new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.verticalAlignment = GridData.FILL;
	    data.horizontalSpan = 3;
	    data.grabExcessHorizontalSpace = true;
	    data.grabExcessVerticalSpace = true;
	    compositeForConcept.setLayoutData(data);
	    
	    Label labelConceptName = new Label(compositeForConcept, SWT.NONE);
	    labelConceptName.setText(Constants.LABEL_CONCEPT_NAME);
	    
	    textConceptName = new Text(compositeForConcept, SWT.BORDER);
	    textConceptName.setText(Constants.EMPTY_STRING);
	    data = new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.grabExcessHorizontalSpace = true;
	    textConceptName.setLayoutData(data);
	    
	    Label labeldummy = new Label(compositeForConcept, SWT.NONE); //For Alignment purpose
	    labeldummy.setText(Constants.EMPTY_STRING);
	    
	    Label labelSelectConceptFields = new Label(shell, SWT.NONE);
	    labelSelectConceptFields.setText(Constants.LABEL_FIELD_AND_CONCEPT);
	    data = new GridData();
	    data.verticalAlignment = SWT.TOP;
	    labelSelectConceptFields.setLayoutData(data);
	    
	    Composite comp = new Composite(shell, SWT.NONE);
	    layout = new GridLayout();
	    layout.numColumns = 1;
	    comp.setLayout(layout);
	    
	    data = new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.verticalAlignment = GridData.FILL;
	    data.horizontalSpan = 2;
	    data.grabExcessHorizontalSpace = true;
	    data.grabExcessVerticalSpace = true;
	    comp.setLayoutData(data);
	    
	    //The table for displaying fields
	    final ConceptsTable objConceptsTable = new ConceptsTable(comp);
	    final Table table = objConceptsTable.getTableViewer().getTable();
	    table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
		        TableItem item =(TableItem)event.item;
		        if(!item.getChecked())
		        	objConceptsTable.getTableViewer().getCellModifier().modify(item, Constants.TABLE_HEADER_NON_NULL, false);
		        
				int count = 0;
				for (int i = 0; i < table.getItemCount(); i++) {
					if (table.getItems()[i].getChecked()) {
						((ConceptsRecord)objConceptsTable.getConceptsList().getConcepts().get(i)).setSelect(true);
						count++;
					} else {
						((ConceptsRecord)objConceptsTable.getConceptsList().getConcepts().get(i)).setSelect(false);
					}
				}
		        //If Count >= 3 disable Reorder Type
				if(count >= 3){
					textReOrderType.setEnabled(false);
				} else {
					textReOrderType.setEnabled(true);
				}
			}
		});
	    
	    table.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				Rectangle clientArea = table.getClientArea();
				Point pt = new Point(event.x, event.y);
				int index = table.getTopIndex();
				while (index < table.getItemCount()) {
					boolean visible = false;
					TableItem item = table.getItem(index);
					for (int i = 0; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							if (i == 1){
								ConceptsRecord concept = (ConceptsRecord)objConceptsTable.getConceptsList().getConcepts().get(index);
								concept.setSelect(item.getChecked());
								if(concept.isNonNull())
									objConceptsTable.getTableViewer().getCellModifier().modify(item, Constants.TABLE_HEADER_NON_NULL, false);
								else if(item.getChecked()) {
									objConceptsTable.getTableViewer().getCellModifier().modify(item, Constants.TABLE_HEADER_NON_NULL, true);
								}
							}
						}
						if (!visible && rect.intersects(clientArea)) {
							visible = true;
						}
					}
					if (!visible)
						return;
					index++;
				}
			}
		});
		
		//Group Force Match ---- START
		Group grpForceMatch = new Group(shell, SWT.NONE);
		grpForceMatch.setText(Constants.LABEL_FORCE_MATCH);

		GridLayout gridLayoutOwnerInfo = new GridLayout();
		gridLayoutOwnerInfo.numColumns = 3;
		gridLayoutOwnerInfo.makeColumnsEqualWidth = true;
		grpForceMatch.setLayout(gridLayoutOwnerInfo);

		GridData gridForceMatch = new GridData();
		gridForceMatch.horizontalAlignment = SWT.FILL;
		gridForceMatch.grabExcessHorizontalSpace = true;
		gridForceMatch.grabExcessVerticalSpace = true;
		gridForceMatch.horizontalSpan = 3;
		grpForceMatch.setLayoutData(gridForceMatch);

		Label labelEffectOnSpecificity = new Label(grpForceMatch, SWT.NONE);
		labelEffectOnSpecificity.setText(Constants.LABEL_EFFECT_ON_SPECIFICITY);

		comboEffectOnSpecificity = new Combo(grpForceMatch, SWT.NONE);
		comboEffectOnSpecificity.setItems(new String[] {Constants.COMBO_VALUE_POSTIVE_CONTRIBUTION_TO_MATCH_SCORE, Constants.COMBO_VALUE_NO_NEGATIVE_CONTRIBUTION_TO_MATCH_SCORE});
	    data = new GridData();
	    data.horizontalAlignment = SWT.FILL;
	    data.grabExcessHorizontalSpace = true;
	    comboEffectOnSpecificity.setLayoutData(data);
	    
	    Label msg = new Label(grpForceMatch, SWT.NONE);
	    msg.setText(Constants.MSG_NO_SUBOPTION_IMPLIES);
	    msg.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
		
		Label labelThreshold = new Label(grpForceMatch, SWT.NONE);
		labelThreshold.setText(Constants.LABEL_THRESHOLD);

		textThreshold = new Text(grpForceMatch, SWT.SINGLE | SWT.BORDER);
		textThreshold.setText(Constants.EMPTY_STRING);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.horizontalAlignment = GridData.FILL;
		textThreshold.setLayoutData(data);
		
		Label labelDummy = new Label(grpForceMatch, SWT.NONE); //For Alignment purpose
	    labelDummy.setText(Constants.EMPTY_STRING);
	    
		//Group Force Match ---- END
		
		//Group Bag of Words ---- START
		Group grpBagOfWords = new Group(shell, SWT.NONE);
		grpBagOfWords.setText(Constants.GRP_BAG_OF_WORDS);

		GridLayout gridBagOfWords = new GridLayout();
		gridBagOfWords.numColumns = 3;
		gridBagOfWords.makeColumnsEqualWidth = true;
		grpBagOfWords.setLayout(gridBagOfWords);

		GridData gridDataBagOfWords = new GridData();
		gridDataBagOfWords.horizontalAlignment = SWT.FILL;
		gridDataBagOfWords.grabExcessHorizontalSpace = true;
		gridDataBagOfWords.grabExcessVerticalSpace = true;
		gridDataBagOfWords.horizontalSpan = 3;
		grpBagOfWords.setLayoutData(gridDataBagOfWords);
		
		Label labelUseBagOfWords = new Label(grpBagOfWords, SWT.NONE);
		labelUseBagOfWords.setText(Constants.LABEL_BAG_OF_WORDS);
		
		final Button btnCheck = new Button(grpBagOfWords, SWT.CHECK);
		data = new GridData();
		data.horizontalSpan = 2;
		btnCheck.setLayoutData(data);
		
		Label labelReOrderType = new Label(grpBagOfWords, SWT.NONE);
		labelReOrderType.setText(Constants.LABEL_REORDER_TYPE);
		
		textReOrderType = new Text(grpBagOfWords, SWT.SINGLE | SWT.BORDER);
		textReOrderType.setText(Constants.EMPTY_STRING);
		data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		data.grabExcessHorizontalSpace = true;
		textReOrderType.setLayoutData(data);
		
		//Group Bag of Words ---- END
		
		//Composite for the remaining fields(Segment Type, Scale, Specificity, Switch Value)
		Composite compositeForSegmentTypes = new Composite(shell, SWT.NONE);
		layout = new GridLayout();
	    layout.numColumns = 3;
	    layout.makeColumnsEqualWidth = true;
	    compositeForSegmentTypes.setLayout(layout);
	    
	    data = new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.verticalAlignment = GridData.FILL;
	    data.horizontalSpan = 3;
	    data.grabExcessHorizontalSpace = true;
	    data.grabExcessVerticalSpace = true;
	    compositeForSegmentTypes.setLayoutData(data);
		
	    //Segment Type
		Label labelSegmentType = new Label(compositeForSegmentTypes, SWT.NONE);
		labelSegmentType.setText(Constants.LABEL_SEGMENT_TYPE);
	    
		comboSegmentType = new Combo(compositeForSegmentTypes, SWT.NONE);
		comboSegmentType.setItems( new String[] {Constants.COMBO_VALUE_CONCATSEG, Constants.COMBO_VALUE_GROUPSEG} );
	    data = new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.grabExcessHorizontalSpace = true;
	    comboSegmentType.setLayoutData(data);
	    
	    labeldummy = new Label(compositeForSegmentTypes, SWT.NONE); //For Alignment purpose
	    labeldummy.setText(Constants.EMPTY_STRING);
	    
	    //Scale
	    Label labelScale = new Label(compositeForSegmentTypes, SWT.NONE);
	    labelScale.setText(Constants.LABEL_SCALE);
	    
		comboScale = new Combo(compositeForSegmentTypes, SWT.NONE);
		comboScale.setItems( new String[] {Constants.COMBO_VALUE_NEVER, Constants.COMBO_VALUE_ALWAYS, Constants.COMBO_VALUE_MATCH } );
	    data = new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.grabExcessHorizontalSpace = true;
	    comboScale.setLayoutData(data);
	    
	    labeldummy = new Label(compositeForSegmentTypes, SWT.NONE); //For Alignment purpose
	    labeldummy.setText(Constants.EMPTY_STRING);
	    
	    //Specificity
	    Label labelSpecificity = new Label(compositeForSegmentTypes, SWT.NONE);
	    labelSpecificity.setText(Constants.LABEL_SPECIFICITY);
	    
	    textSpecificity = new Text(compositeForSegmentTypes, SWT.BORDER);
	    textSpecificity.setText(Constants.EMPTY_STRING);
	    data = new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.grabExcessHorizontalSpace = true;
	    textSpecificity.setLayoutData(data);
	    
	    labeldummy = new Label(compositeForSegmentTypes, SWT.NONE); //For Alignment purpose
	    labeldummy.setText(Constants.EMPTY_STRING);
		
	    //Switch Value
	    Label labelSwitchValue = new Label(compositeForSegmentTypes, SWT.NONE);
	    labelSwitchValue.setText(Constants.LABEL_SWITCH_VALUE);
	    
	    textSwitchValue = new Text(compositeForSegmentTypes, SWT.BORDER);
	    textSwitchValue.setText(Constants.EMPTY_STRING);
	    data = new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.grabExcessHorizontalSpace = true;
	    textSwitchValue.setLayoutData(data);
	    
	    labeldummy = new Label(shell, SWT.NONE); //For Alignment purpose
	    labeldummy.setText(Constants.EMPTY_STRING);
	    
	    //Separator
	    Label shadow_sep_h = new Label(shell, SWT.SEPARATOR | SWT.SHADOW_OUT | SWT.HORIZONTAL);
	    data = new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.horizontalSpan = 3;
	    shadow_sep_h.setLayoutData(data);
	    
	    // Composite for holding Buttons(Ok, Cancel)
	    comp = new Composite(shell, SWT.NONE);
	    layout = new GridLayout();
	    layout.numColumns = 2;
	    layout.makeColumnsEqualWidth = true;
	    comp.setLayout(layout);
	    
	    data = new GridData();
	    data.horizontalAlignment = GridData.FILL;
	    data.verticalAlignment = GridData.FILL;
	    data.horizontalSpan = 3;
	    data.grabExcessHorizontalSpace = true;
	    comp.setLayoutData(data);
	    
	    Button btnOk = new Button(comp, SWT.PUSH);
	    btnOk.setText(Constants.BTN_OK);
		data = new GridData();
		data.horizontalAlignment = GridData.END;
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 80;
		btnOk.setLayoutData(data);
		btnOk.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				System.out.println("Concept Name: "+textConceptName.getText());
				System.out.println("Effect on Specificity : "+comboEffectOnSpecificity.getText());
				System.out.println("Threshold: "+textThreshold.getText());
				System.out.println("Bag of Words: "+btnCheck.getSelection());
				System.out.println("ReOrder Type: "+textReOrderType.getText());
				System.out.println("Segment Type: "+comboSegmentType.getText());
				System.out.println("Scale: "+comboScale.getText());
				System.out.println("Specificity Type: "+textSpecificity.getText());
				System.out.println("Switch Value: "+textSwitchValue.getText());
				
				if(objConceptsTable.getConceptsList().getConcepts() != null && objConceptsTable.getConceptsList().getConcepts().size() > 0) {
					for (Iterator<ConceptsRecord> iterator = objConceptsTable.getConceptsList().getConcepts().iterator(); iterator.hasNext();) {
						ConceptsRecord obj = (ConceptsRecord) iterator.next();
						System.out.println("*******************");
						System.out.println("Selected: "+obj.isSelect());
						System.out.println("Children: "+obj.getChildren());
						System.out.println("Non Null: "+obj.isNonNull());
						System.out.println("*******************");
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		Button btnCancel = new Button(comp, SWT.PUSH);
		btnCancel.setText(Constants.BTN_CANCEL);
		data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.widthHint = 80;
		btnCancel.setLayoutData(data);
		
		//Check if Selected Items > 3. If yes, disable ReOrderType Text Box.
		if(objConceptsTable.getConceptsList().getConcepts() != null && objConceptsTable.getConceptsList().getConcepts().size() > 0){
	    	int count = 0;
	    	for (Iterator<ConceptsRecord> iterator = objConceptsTable.getConceptsList().getConcepts().iterator(); iterator.hasNext();) {
				ConceptsRecord obj = (ConceptsRecord) iterator.next();
				if(obj.isSelect())
					count++;
	    	}
	    	
	    	//If Count >= 3 disable Reorder Type
			if(count >= 3){
				textReOrderType.setEnabled(false);
			} else {
				textReOrderType.setEnabled(true);
			}
	    }
		
	}
	
	public void run() {
		
		Display display = new Display();
	    Shell shell = new Shell(display);
	    shell.setText(Constants.ADD_CONCEPTS_TITLE);
	    shell.setSize(800, 550);
	    GridLayout layout = new GridLayout();
	    layout.numColumns = 3;
	    layout.marginLeft = 10;
	    layout.marginRight = 10;
	    layout.makeColumnsEqualWidth = true;
	    shell.setLayout(layout);
	    
	    addChildControls(shell);
		
	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch())
	        display.sleep();
	    }
	    display.dispose();
	}
	
	public static void main(String[] args) {
		
		new ConfigureConceptsUI().run();
	}
	
}
