/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venky.poi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 *
 * @author venky
 */
public class BeanReader<B> extends BeanIntrospector<B>{
    
    private Sheet sheet;
    private Iterator<Row> rowIterator; 
    private String[] heading;
    protected String[] getHeading(){
    	return heading;
    }
    
    public BeanReader(Workbook book, String sheetName, Class<B> beanClass){
        this(book.getSheet(sheetName),beanClass);
    }
    public BeanReader(Workbook book, int sheetIndex,Class<B> beanClass){
        this(book.getSheetAt(sheetIndex),beanClass);
    }
    public BeanReader(Sheet beanSheet,Class<B> beanClass){
        super(beanClass);
        this.sheet = beanSheet;
        this.rowIterator = sheet.iterator();
        
        Row header = getNextRow();
        if (header != null){
            heading = new String[header.getLastCellNum()]; // as cell indexes start at zero,LastCellNum can be seen as Size of row  
            for (int i = 0 ; i < heading.length ; i ++ ){
                heading[i] = header.getCell(i).getStringCellValue();
            }
        }
    }
    public B getNextRecord(){ 
        B b = null;
        Row row = getNextRow();
        if (row != null){
            b = createInstance(); 
            fillBeanValues(b, row);
        }
        return  b;
    }
    public CellStyle getHeaderStyle(){ 
        return sheet.getRow(0).getCell(0).getCellStyle();
    }
    
    public List<B> getAllUnreadRecords(){
        return getNextRecords(-1);
    }
    public List<B> getNextRecords(int iNumRecs){
        List<B> records = (iNumRecs > 0) ? new ArrayList<B>(iNumRecs) : new ArrayList<B>();
        int numRecordsAdded = 0;
        while ((iNumRecs < 0 || numRecordsAdded < iNumRecs) && hasMoreRecords()){
            records.add(getNextRecord()); 
            numRecordsAdded ++;
        } 
        return records;
    }

    private boolean hasMoreRecords(){ 
        return rowIterator.hasNext();
    }
    private Row getNextRow(){
        return (rowIterator.hasNext() ? rowIterator.next() : null); 
    }
    
    protected void fillBeanValues(B b, Row row){
        for (int i = 0 ; i < heading.length ; i ++ ){ 
            Method getter = getGetter(heading[i]);
            if (getter == null) {
                throw new RuntimeException("Getter not found for " + heading[i]);
            }
            Method setter = getMethod("set"+heading[i],getter.getReturnType());
            if (setter == null){
                throw new RuntimeException("Setter not found for " + heading[i]);
            }
            Cell cell = row.getCell(i);
            Object value = null;
            if (isNumeric(getter.getReturnType())) {
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_NUMERIC:
                    case Cell.CELL_TYPE_FORMULA: 
                        value = cell.getNumericCellValue();
                        break;
                    default :
                        value = Double.valueOf(cell.getStringCellValue());
                        break;
                }
                if (int.class.isAssignableFrom(getter.getReturnType())){
                    value = ((Double)value).intValue();
                }else if (float.class.isAssignableFrom(getter.getReturnType())) {
                    value = ((Double)value).floatValue();
                }
            }else if (isDate(getter.getReturnType())){
                value = cell.getDateCellValue();
            }else if (isBoolean(getter.getReturnType())){
                value = cell.getBooleanCellValue() ;
            }else {
                value = cell.getStringCellValue();
            }
            try {
                setter.invoke(b, value);
            }catch (Exception e){
                throw new RuntimeException("Cannot set " + heading[i] + " as " + value + " of Class " + value.getClass().getName(), e);
            }
        }
    }
}
