/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venky.poi;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 *
 * @author venky
 */
public class BeanWriter<B> extends BeanIntrospector<B> {
    private Sheet sheet;
    private int numBeansWriten = 0;
    private String[] headings ;
    public BeanWriter(Class<B> beanClass, Sheet sheet, CellStyle headerStyle, String... headings){
        super(beanClass);
        this.sheet = sheet; 
        this.headings = (headings == null || headings.length == 0)? extractHeadings(beanClass) : headings;
        writeHeadings(headerStyle);
    }
    private String[] extractHeadings(Class<B> beanClass){ 
        Set<String> headers = new HashSet<String>(); 
        for (Method m:beanClass.getDeclaredMethods()){
            String headerName = null;
            if (m.getName().startsWith("get") && m.getParameterTypes().length == 0) {
                headerName = m.getName().substring(3);
                if (headers.contains(headerName)){
                    continue;
                }
                if (getMethod("set"+headerName, m.getReturnType()) != null){ 
                    headers.add(headerName);
                }
            }else if (m.getName().startsWith("set") && m.getParameterTypes().length == 1) {
                headerName = m.getName().substring(3);
                if (headers.contains(headerName)){
                    continue;
                }
                if (getGetter(headerName) != null) {
                    headers.add(headerName);
                } 
            }
        }
        return headers.toArray(new String[] {});
    }
    public void write(List<B> beans){
        for (B bean:beans){
            write(bean);
        }
    }
    public void write(B bean){
        Row row = sheet.createRow(1 + numBeansWriten);
        fillRow(row,bean);
        numBeansWriten ++;
    }
    
    private void writeHeadings(CellStyle headerStyle){ 
        Row row = sheet.createRow(0);
        for (int i = 0 ; i < headings.length ; i++ ){
            Cell cell = row.createCell(i);
            cell.setCellValue(headings[i]);
            cell.setCellStyle(headerStyle);
        }
    }
    private void fillRow(Row row,B bean){
        for (int i = 0 ; i < headings.length ; i++ ){
            String heading = headings[i];
            Method getter = getGetter(heading);
            if (getter == null){
            	continue;
            }
            Object value  = null;
            try {
                value = getter.invoke(bean, new Object[]{});
                Cell cell = row.createCell(i);
                if (isNumeric(getter.getReturnType())){
                    cell.setCellValue(Double.valueOf(String.valueOf(value)));
                }else if (isDate(getter.getReturnType())) {
                    cell.setCellValue((Date)value);
                }else if (isBoolean(getter.getReturnType())) {
                    cell.setCellValue((Boolean)value);
                }else{
                    cell.setCellValue(String.valueOf(value));
                }
            }catch (Exception e){
                e.printStackTrace();
                throw new RuntimeException("Cannot set " + heading + " value " + String.valueOf(value),e );
            }
        }
        
    }
}
