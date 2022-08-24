
/**
 *
 * @author Nathan Magnus, under the supervision of Howard Hamilton
 * Copyright: University of Regina, Nathan Magnus and Su Yibin, June 2009.
 * No reproduction in whole or part without maintaining this copyright notice
 * and imposing this condition on any subsequent users.
 *
 * File: DIC.java
 * Input files needed:
 *      1. config.txt - four lines, each line is an integer
 *          line 1 - number of items per transaction
 *          line 2 - number of transactions
 *          line 3 - minsup
 *          line 4 - M value (number of transactions read before reevaluating the status of each Candidate)
 *      2. transa.txt - transaction file, each line is a transaction, items are separated by a space
 */

package dic;

import java.util.*;
import java.io.*;

public class DIC
{    
    public static void main(String[] args)
    {
        DIC nd = new DIC();
        nd.init();
    }
    //for potential applet implementation
    public void init()
    {
        DICCalculation dp = new DICCalculation();
        dp.dicProcess();
    }
}

/******************************************************************************
 * Class Name   : DICCalculation
 * Purpose      : generate itemsets using dic algorithm
 *****************************************************************************/
class DICCalculation
{
    int numItems; //number of items per transaction
    int numTransactions;//number of transactions
    int M;//number of transactions between calculations of square/circle/solid/dashed
    int numRead; //number of transactions read from transaction s
    int dbRun=0; //the number of times the program has run through the database. This is used to determine if an itemset is to become solid or not
    double minSup; //minimum support required for an item to be frequent
    String transaFile="transa.txt"; //transaction file
    String configFile="config.txt"; //configuration file
    String outputFile="dic-output.txt"; //output file
    Vector<Candidate> candidates = new Vector<Candidate>(); //all possible candidates at any layer of the lattice
    String oneVal[]; //array of value per column that will be treated as a '1'
    String itemSep = " "; //what characater(s) separate items in the transaction file

    /******************************************************************************
    * Class Name   : Candidate
    * Purpose      : hold Candidate specific information
    *****************************************************************************/
    class Candidate
    {
        int count; //the number of matching transactions
        int startNum; //the numRead which this Candidate was created
        int dbRun; //the run through the db when this Candidate was added
        int itemsetNum; //the layer in the lattice that this Candidate is in
        String itemset; //the string representation of this itemset
        boolean circle; //if it is a circle or not
        boolean solid; //if it is solid or not

        /************************************************************************
        * Constructor  : Candidate
        * Purpose      : create and initialize a Candidate with user specified values
        * Parameters   : count - integer representing how many times the item has occured in the database
        *              : itemset - string representation of the items in the Candidate (ie: '1 2' or '1 44 67'
        *              : dbRun - the run through the database which the itemset was added (due to new squares on previous lattice layers)
        *              : startNum - the number of transactions read before the Candidate was created
        *              : itemsetNum - the layer in the lattice that this Candidate is in
        * Return       : None
        *************************************************************************/
        public Candidate(int count, String itemset, int dbRun, int startNum, int itemsetNum)
        {
            //assign values to the Candidate member variables
            this.count = count;
            this.itemset = itemset;
            this.circle = true;
            this.solid = false;
            this.startNum = startNum;
            this.dbRun = dbRun;
            this.itemsetNum = itemsetNum;
        }
    }

    /************************************************************************
     * Method Name  : dicProcess
     * Purpose      : run the DIC algorithm
     * Parameters   : None
     * Return       : None
     *************************************************************************/
    public void dicProcess()
    {
        //FileInputStream and BufferedReader for the transaction file
        FileInputStream file_in;
        BufferedReader data_in;
        Date d;
        Candidate cand; //temporary Candidate for initial populating of Candidate list
        int latticeLayer=0; //the layer on the lattice to be displayed
        long start, end; //start and end time of the algorithm

        //load config
        getConfig();

        System.out.println("DIC algorithm has started.\n");

        //get the start time
        d = new Date();
        start = d.getTime();

        //populate 1-itemsets
        for(int i=1; i<=numItems; i++)
        {
            cand = new Candidate(0, Integer.toString(i), 0, numTransactions, 1);
            candidates.add(cand);
        }

        do
        {
            dbRun++; //increase the number of times database has been traversed
            try
            {
                numRead=0;//reset the number of items read from the database
                //open the transaction file to allow reading
                file_in = new FileInputStream(transaFile);
                data_in = new BufferedReader(new InputStreamReader(file_in));

                //load m transactions at a time, compare them to the itemsets and update the candidates
                while(numRead<numTransactions)
                {
                    loadTransactions(data_in);
//                    System.out.println("Database Run = " + dbRun + "  numRead = " + numRead);
                    updateCandidates();
                }
//                System.out.println("Now displaying.");
                //increase the layer on the latice display that layer and remove it
                latticeLayer++;
                display(latticeLayer);
                //remove old itemsets to help clean up memory a bit
                removeItemsets(latticeLayer);
            }
            catch(IOException e)
            {
                System.out.println(e);
            }
        }while(dashFound()); //keep looping until there are no more dashed candidates

        //display any leftover lattice Layers
        while(candidates.size()>0 && latticeLayer<numItems)
        {
            latticeLayer++;
//            System.out.println(latticeLayer + " lattice layer deleted. candidates has " + candidates.size() + " itemsets left");

            display(latticeLayer);
            removeItemsets(latticeLayer);
        }
        
        //get the end time
        d = new Date();
        end = d.getTime();

        //display the running time
        System.out.println("Execution time is: " + (end-start)/1000.0 + " seconds.");
    }
    
    /************************************************************************
     * Method Name  : getInput
     * Purpose      : get user input from System.in
     * Parameters   : None
     * Return       : String value of the users input
     *************************************************************************/
    public static String getInput()
    {
        String input="";
        //read from System.in
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //try to get users input, if there is an error print the message
        try
        {
            input = reader.readLine();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return input;
    }

    
    /************************************************************************
     * Method Name  : getConfig
     * Purpose      : get the configuration information (config filename, transaction filename)
     *              : configFile and transaFile will be change appropriately
     * Parameters   : None
     * Return       : None
     *************************************************************************/
    private void getConfig()
    {
        FileWriter fw;
        BufferedWriter file_out;

        String input="";
        //ask if want to change the config
        System.out.println("Default Configuration: ");
        System.out.println("\tRegular transaction file with '" + itemSep + "' item separator.");
        System.out.println("\tConfig File: " + configFile);
        System.out.println("\tTransa File: " + transaFile);
        System.out.println("\tOutput File: " + outputFile);
        System.out.println("\nPress 'C' to change the item separator, configuration file and transaction files");
        System.out.print("or any other key to continue.  ");
        input=getInput();

        if(input.compareToIgnoreCase("c")==0)
        {
            System.out.print("Enter new transaction filename (return for '"+transaFile+"'): ");
            input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                transaFile=input;

            System.out.print("Enter new configuration filename (return for '"+configFile+"'): ");
            input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                configFile=input;

            System.out.print("Enter new output filename (return for '"+outputFile+"'): ");
            input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                outputFile=input;

            System.out.println("Filenames changed");

            System.out.print("Enter the separating character(s) for items (return for '"+itemSep+"'): ");
            input=getInput();
            if(input.compareToIgnoreCase("")!=0)
                itemSep=input;

            
        }
            
        try
        {
             FileInputStream file_in = new FileInputStream(configFile);
             BufferedReader data_in = new BufferedReader(new InputStreamReader(file_in));
             //number of items
             numItems=Integer.valueOf(data_in.readLine()).intValue();

             //number of transactions
             numTransactions=Integer.valueOf(data_in.readLine()).intValue();

             //minsup
             minSup=(Double.valueOf(data_in.readLine()).doubleValue());

             //get the M value
             M=(Integer.valueOf(data_in.readLine()).intValue());

             //output config info to the user
             System.out.print("\nInput configuration: "+numItems+" items, "+numTransactions+" transactions, ");
             System.out.println("minsup = "+minSup+"%");
             System.out.println();
             minSup/=100.0;

            oneVal = new String[numItems];
            System.out.print("Enter 'y' to change the value each row recognizes as a '1':");
            if(getInput().compareToIgnoreCase("y")==0)
            {
                for(int i=0; i<oneVal.length; i++)
                {
                    System.out.print("Enter value for column #" + (i+1) + ": ");
                    oneVal[i] = getInput();
                }
            }
            else
                for(int i=0; i<oneVal.length; i++)
                    oneVal[i]="1";

            //create the output file
            fw= new FileWriter(outputFile);
            file_out = new BufferedWriter(fw);
            //put the number of transactions into the output file
            file_out.write(numTransactions + "\n");
            file_out.write(numItems + "\n******\n");
            file_out.close();
        }
        //if there is an error, print the message
        catch(IOException e)
        {
            System.out.println(e);
        }
    }

    /************************************************************************
     * Method Name  : dashFound
     * Purpose      : determine if there are any dashed candidates still in the lattice
     * Parameters   : None
     * Return       : boolean true if there is a dashed Candidate in the lattice, false otherwise
     *************************************************************************/
    private boolean dashFound()
    {
        //check every Candidate in the list to see if it is not solid (dashed)
        for(int c=0; c<candidates.size(); c++)
            if(!candidates.get(c).solid)
                return true; //return true if there is a dashed item
        return false; //false otherwise
    }

    /************************************************************************
     * Method Name  : loadTransactions
     * Purpose      : load M transactions from the transactio file and increment Candidate counts appropriately
     * Parameters   : data_in - BefferedReader of the transaction file
     * Return       : None
     *************************************************************************/
    private void loadTransactions(BufferedReader data_in)
    {
        StringTokenizer st; //string tokenizer
        boolean match; //used to determine if each item of the Candidate is present
        boolean trans[] = new boolean[numItems]; //array that holds a transaction
        String input="f"; //input from the file, initially set to "f" so that passed initial while loop condition
        
        try
        {
            //load transactions and compare to existing candidates of the n-itemsets
            for(int t=0; t<M; t++)
            {
                //System.out.println("Got here " + numRead + " times");
                input = data_in.readLine(); //get a transaction from the file
                if(input==null)
                    break;

                //System.err.println(input);
                //increase the number of transactions read
                numRead++;
                //create the new tokenizer
                st = new StringTokenizer(input, itemSep);
                
                //populate array for this transaction
                for(int i=0; i<numItems; i++)
                    trans[i]=(st.nextToken().compareToIgnoreCase(oneVal[i])==0);
                
                //for each Candidate
                for(int c=0; c<candidates.size(); c++)
                {
                    match = false;
                    //if the Candidate still needs to be confirmed
                    if(!candidates.get(c).solid)
                    {
                        st = new StringTokenizer(candidates.get(c).itemset); //tokenize the Candidate to get the items out
                        //check to see if each item is present in transaction
                        while(st.hasMoreTokens())
                        {
                            match=(trans[Integer.valueOf(st.nextToken())-1]);
                            if(!match)
                                break;
                        }
                        //if all of the items were in the transaction, increment count
                        if(match)
                            candidates.get(c).count++;
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }

    /************************************************************************
     * Method Name  : updateCandidates
     * Purpose      : update Candidate information (if it is solid/dashed, circle/square,
     *              : if it is square, create new candidates appropriately
     * Parameters   : None
     * Return       : None
     *************************************************************************/
    private void updateCandidates()
    {
        //for each Candidate
        for(int c=0; c<candidates.size(); c++)
        {
            //if its seen all the transactions in the database once, make it solid
            if(!candidates.get(c).solid && candidates.get(c).startNum==numRead && candidates.get(c).dbRun==dbRun-1)
            {
//                System.out.println("Candidate '" + candidates.get(c).itemset + "' is now solid");
                candidates.get(c).solid=true;
            }
            
            //if it is frequent make it a square
            if(candidates.get(c).circle && candidates.get(c).count/(double)numTransactions >= minSup)
            {
//                System.out.println("Candidate '" + candidates.get(c).itemset + "' is now square with a count of " + candidates.get(c).count);
                candidates.get(c).circle=false;
                makeNewCandidates(candidates.get(c));
            }
        }
    }

    /************************************************************************
     * Method Name  : isDuplicateCandidate
     * Purpose      : determine if a Candidate already exists
     * Parameters   : cand - string representation of the Candidate to be checked
     * Return       : boolean true if the Candidate is a duplicate, false otherwise
     *************************************************************************/
    private boolean isDuplicateCandidate(String cand)
    {
        //check each Candidate to see if cand is the same. If it is, return true.
        for(int c=0; c<candidates.size(); c++)
            if(candidates.get(c).itemset.compareToIgnoreCase(cand)==0)
                return true;
        //if nothing matches, return false
        return false;
    }

    /************************************************************************
     * Method Name  : makeNewCandidates
     * Purpose      : make all possible combinations of new candidates (relating to the passed Candidate)
     *              : one level up the lattice
     * Parameters   : cand - a Candidate
     * Return       : None
     *************************************************************************/
    private void makeNewCandidates(Candidate cand)
    {
        String newCandidateString; //the string representation of the new Candidate
        Candidate tempCand, secondCand; //temporary Candidate and Candidate being used with cand to create new Candidate
        StringTokenizer st1, st2; //string tokenizers
        boolean itemsInSet[]; //items in a set, used to sort Candidate orders and ensure there are not duplicates

        for(int c=0; c<candidates.size(); c++)
        {
            newCandidateString = new String(); //empty the Candidate string
            secondCand = candidates.get(c); //assign the Candidate being combined with cand
            //if secondCand is in the same lattice level as cand, and secondCand is a square
            if(secondCand.itemsetNum == cand.itemsetNum && !secondCand.circle)
            {
               // System.out.println("Cand1 itemsetNum: " + cand.itemsetNum + " secondCand itemsetNum: " + secondCand.itemsetNum);
                //set up tokenizers
                st1 = new StringTokenizer(cand.itemset);
                st2 = new StringTokenizer(secondCand.itemset);

                //sort the two items in the two candidates so that the itemsets come out in numerical order
                //ie: cand:'1 3', secondCand:'1 2' becomes '1 2 3' not '1 3 2'
                itemsInSet = new boolean[numItems];
                while(st1.hasMoreTokens())
                {
                    itemsInSet[Integer.valueOf(st1.nextToken())-1]=true;
                    itemsInSet[Integer.valueOf(st2.nextToken())-1]=true;
                }
                //create the Candidate string and trim it
                for(int i=0; i<numItems; i++)
                    if(itemsInSet[i])
                        newCandidateString += Integer.valueOf(i+1).toString() + " ";
                
                newCandidateString = newCandidateString.trim();

                //recreate the first tokenizers so that st1.countTokens() returns the correct result
                st1 = new StringTokenizer(cand.itemset);
                //tokenize the new Candidate string
                st2 = new StringTokenizer(newCandidateString);
                //if the Candidate is not a duplicate of an existing Candidate and only one new item is added to make the new Candidate, add it to the Candidate vector
                if(!isDuplicateCandidate(newCandidateString) && st2.countTokens()==st1.countTokens()+1)
                {
                    tempCand = new Candidate(0, newCandidateString, dbRun, numRead, cand.itemsetNum+1);
//                    System.out.println("The new Candidate is '" + tempCand.itemset + "' with itemsetNum of " + tempCand.itemsetNum);
                    candidates.add(tempCand);
                }
            }
        }
    }
    
   /************************************************************************
     * Method Name  : display
     * Purpose      : display all frequent n-itemsets
     * Parameters   : n - the n-itemset to be displayed
     * Return       : None
     *************************************************************************/
    public void display(int n)
    {
        //Output file
        FileWriter fw;
        BufferedWriter file_out;
        Candidate cand; //temporary holder for a Candidate
        Vector<String> itemsets = new Vector<String>(); //holder, for display purposes, of the itemset string representations
        boolean setExists = false; //if the set exists or not (aesthetics to prevent empty sets
        //open output file
        try
        {
            fw= new FileWriter(outputFile, true);
            file_out = new BufferedWriter(fw);
            //check each Candidate
            for(int c=0; c<candidates.size(); c++)
            {
                cand = candidates.get(c);
    //            System.err.println("Candidate: " + cand.dbRun + " actual: " + dbRun + " solid: " + cand.solid + " circle: " + cand.circle);
                //if it is in the n-itemset and is confirmed frequent
                if(cand.itemsetNum==n && cand.solid && !cand.circle)
                {
                    setExists=true; //there is at least one itemset
                    itemsets.add(cand.itemset); // add the item to the string vector to be displayed
                    //add to output file
                    file_out.write(cand.itemset + "," + (double)cand.count/numTransactions + "\n");
                }
            }
            file_out.write("-\n");
            //if there is at least one itemset, display the itemsets
            if(setExists)
            {
                System.out.println("Frequent " + n + "-itemsets:");
                System.out.println(itemsets);
                itemsets.clear();
            }
            file_out.close();
        }
        catch(IOException e)
        {
            System.out.println(e);
        }
    }

    /************************************************************************
     * Method Name  : removeItemsets
     * Purpose      : remove old itemsets from the vector to free up memory
     * Parameters   : n - the layer of the lattice which is to be removed
     * Return       : None
     *************************************************************************/
    private void removeItemsets(int n)
    {
        //check each Candidate to see if its in the itemset to delete, if so, delete it
        for(int c=candidates.size()-1; c>=0; c--)
            if(candidates.get(c).itemsetNum<=n)
                candidates.remove(c);
    }
}
