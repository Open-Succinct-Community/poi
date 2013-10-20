/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.venky.poi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author venky
 */
public class BeanReaderTest {
    
    public BeanReaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    private InputStream is ; 
    private Workbook book ;
    private BeanReader<Item> instance ;
    @Before
    public void setUp() throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException {
        is = getClass().getResourceAsStream("xls/Item.xls");
        assertTrue (is != null);
        book = new HSSFWorkbook(is);
        instance = new BeanReader<Item>(book, "Items", Item.class);
    }
    
    @After
    public void tearDown() throws IOException {
        if (is !=null) {
            is.close();
        }
    }

    public static class Item {
        int id; 
        String description; 
        int capacityFactor = 1;
        public Item(){
            
        }

        public int getCapacityFactor() {
            return capacityFactor;
        }

        public void setCapacityFactor(int capacityFactor) {
            this.capacityFactor = capacityFactor;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
        
        @Override
        public int hashCode() {
            int hash = 7;
            hash = 59 * hash + this.id;
            hash = 59 * hash + (this.description != null ? this.description.hashCode() : 0);
            hash = 59 * hash + this.capacityFactor;
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Item other = (Item) obj;
            if (this.id != other.id) {
                return false;
            }
            if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
                return false;
            }
            if (this.capacityFactor != other.capacityFactor) {
                return false;
            }
            return true;
        }
    }
    /**
     * Test of getNextRecord method, of class BeanReader.
     * 
     */
    @Test
    public void testGetNextRecord() throws InstantiationException, IllegalAccessException {
        System.out.println("getNextRecord");
        Item expResult = new Item(); 
        expResult.setCapacityFactor(1);
        expResult.setDescription("I10");
        expResult.setId(1);
        Item result = instance.getNextRecord();
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllRecords method, of class BeanReader.
     */
    @Test
    public void testGetAllUnreadRecords() {
        System.out.println("getAllUnreadRecords");
        List<Item> result = instance.getAllUnreadRecords();
        assertEquals(result.size(),4);
    }

    /**
     * Test of getNextRecords method, of class BeanReader.
     */
    @Test
    public void testGetNextRecords() {
        System.out.println("getNextRecords");
        List<Item> result = instance.getNextRecords(2);
        assertEquals(2, result.size());
        result = instance.getNextRecords(2);
        assertEquals(2, result.size());
        result = instance.getNextRecords(2);
        assertEquals(0, result.size());
        result = instance.getNextRecords(1);
        assertEquals(0, result.size());
        result = instance.getAllUnreadRecords(); 
        assertEquals(0, result.size());
    }
}
