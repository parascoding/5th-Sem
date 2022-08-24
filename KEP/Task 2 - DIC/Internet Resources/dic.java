/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dic;

//
//  Author: Su Yibin, under the supervision of Howard Hamilton and
//          Mengchi Liu
//  Copyright: University of Regina and Su Yibin, April 2000
//  No reproduction in whole or part without maintaining this copyright
//  notice and imposing this condition on any subsequent users.

//---- dic.java

//---- input file need:
//----   1. config.txt
//----      four lines, each line a integer
//----      item number, transaction number , minsup , step length
//----   2. transa.txt

import java.io.*;
import java.util.*;

//-------------------------------------------------------------
//  Class Name : dic
//  Purpose    : main program class
//-------------------------------------------------------------


//-------------------------------------------------------------
//  Class Name : dicProcess
//  Purpose    : main processing class
//-------------------------------------------------------------
public class dic {

  public static void main(String[] args) throws IOException
  {
    dicProcess process1=new dicProcess();
    System.exit(0);
  }
}
class dicProcess {

  private final int DC=1; // four states of tree node
  private final int DS=2;
  private final int SC=3;
  private final int SS=4;
  int N; // total number of items
  int M; // total number of transactions
  int stepm; // step increment
  int tid; // current line number of transaction
  int k; // current processing k-itemset
  int setnum; // item number in current transaction
  int minsup; // minimum percent for frequent itemset
  hashtreenode root;
  String DSset,DCset,SCset,SSset;
  String transafile="transa.txt"; //default transaction file
  String configfile="config.txt"; //default configuration file


//-------------------------------------------------------------
//  Class Name : hashtreenode
//  Purpose    : object of node of hash tree
//-------------------------------------------------------------
  class hashtreenode
  {
    int state;         //  should be one of (DC,DS,SC,SS)
    String itemset;    //  itemset that this node stores
    int counter;       //  count the number of occurrence in transactions
    int starting;      //  transaction id when this node starts to be counted
    int startingk;     //  k's value when this node starts to be counted
                       //  k : the number of <stepm>s that have been passed through
    boolean needcounting;  //  if this node should be counted later on
    Hashtable<String, hashtreenode> ht;      //  hash table stores this node's son-nodes

    // constructor 1
    public hashtreenode(int state,String itemset,int counter,int starting,int startingk)
    {
      this.state=state;
      if (itemset==null)
        this.itemset=new String();
      else
        this.itemset=new String(itemset);
      this.counter=counter;
      this.starting=starting;
      this.startingk=startingk;
      needcounting=true;
      ht=new Hashtable<String, hashtreenode>();
    }

    // constructor 2
    public hashtreenode()
    {
      this.state=DC;
      this.itemset=new String();
      this.counter=0;
      this.starting=0;
      this.startingk=0;
      needcounting=true;
      ht=new Hashtable<String, hashtreenode>();
    }
  }

//-------------------------------------------------------------
//  Method Name: getconfig
//  Purpose    : open file config.txt
//             : get the total number of items of transaction file
//             : and the total number of transactions
//             : and minsup
//-------------------------------------------------------------
  public void getconfig() throws IOException
  {
    FileInputStream file_in;
    BufferedReader data_in;
    String oneline=new String();
    int i=0;

    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);
    String response = "";

    // ask if they want to change the config or transaction file, if they do prompt for changes
    System.out.println("Press 'C' to change the default configuration and transaction files");
    System.out.print("or any other key to continue.  ");
    try
    {
      response = reader.readLine();
    }
    catch (Exception e)
    {
      System.out.println(e);
    }

    if(response.compareTo("C") * response.compareTo("c") == 0)
    {
      System.out.print("\nEnter new transaction filename: ");
      try
      {
        transafile = reader.readLine();
      }
      catch (Exception e)
      {
        System.out.println(e);
      }

      System.out.print("Enter new configuration filename: ");
      try
      {
        configfile = reader.readLine();
      }
      catch (Exception e)
      {
        System.out.println(e);
      }
      System.out.println("Filenames changed");
    }

    try
    {
      file_in = new FileInputStream(configfile);
      data_in = new BufferedReader(new InputStreamReader(file_in));

      oneline=data_in.readLine();
      N=Integer.valueOf(oneline).intValue();
      oneline=data_in.readLine();
      M=Integer.valueOf(oneline).intValue();
      oneline=data_in.readLine();
      minsup=Integer.valueOf(oneline).intValue();
      oneline=data_in.readLine();
      stepm=Integer.valueOf(oneline).intValue();

      String user = "";
      while(stepm < 5)
      {
        System.out.println("\nThe value of step M must be at least 5 (it is currently "+stepm+")");
        System.out.print("Enter a larger value for step M: ");
      try
      {
          user = reader.readLine();
      }
      catch (Exception e)
      {
          System.out.println(e);
      }

        stepm = Integer.valueOf(user).intValue();
      }

      System.out.print("\nInput configuration: " + N + " items, " + M + " transactions, ");
      System.out.println("minsup = " + minsup + "%, step M = " + stepm + "\n");

    }
    catch (IOException e)
    {
      System.out.println(e);
    }
  }

//-------------------------------------------------------------
//  Method Name: getitemat
//  Purpose    : get an item from an itemset
//             : get the total number of items of transaction file
//  Parameter  : int i : i-th item ; itemset : string itemset
//  Return     : String : the item at i-th in the itemset
//-------------------------------------------------------------
  public String getitemat(int i,String itemset)
  {
    StringTokenizer st = new StringTokenizer(itemset);
    String str1=new String(itemset);
    /*
    int pos1=0,pos2=0;
    
    for (int a=1;a<i;a++)
    {
      pos1=itemset.indexOf(" ",pos1);
      pos1++;
    }
    pos2=itemset.indexOf(" ",pos1+1);
    if (pos2==-1)
      pos2=itemset.length();
    
    str1=itemset.substring(pos1,pos2);*/
    for(int a=0; a<i; a++)
        str1 = st.nextToken();
    return(str1);
  }

//-------------------------------------------------------------
//  Method Name: itesetsize
//  Purpose    : get item number of an itemset
//  Parameter  : itemset : string it emset
//  Return     : int : the number of item of the itemset
//-------------------------------------------------------------
  public int itemsetsize(String itemset)
  {
    StringTokenizer st=new StringTokenizer(itemset);
    return st.countTokens();
  }

//-------------------------------------------------------------
//  Method Name: dashfound
//  Purpose    : check the hashtree to see if there exsits a dashed node
//             : if no dashed circle or dashed squire exsits in hash tree
//             : program will end
//  Parameter  : htn : hash tree root
//  Return     : boolean : if a dashed node found, return true, otherwise, false
//-------------------------------------------------------------
  boolean dashfound(hashtreenode htn)
  {
    if (htn.state==DS || htn.state==DC)
      return true;
    for (Enumeration e=htn.ht.elements();e.hasMoreElements(); )
      if (dashfound((hashtreenode)e.nextElement()))
        return true;
    return false;
  }

//-------------------------------------------------------------
//  Method Name: printhashtree
//  Purpose    : print the whole hash tree
//  Parameter  : htn is a hashtreenode (when other method call this method,it is the root)
//             : transa : special transaction with all items occurr in it.
//             : a : recursive depth
//  Return     :
//-------------------------------------------------------------
  public void printhashtree(hashtreenode htn,String transa,int a)
  {
    String state=new String();
    switch (htn.state) {
      case DC:
        state="DC" ;
        DCset=DCset.concat(htn.itemset);
        DCset=DCset.concat(",");
        //	DCset=DCset.concat("[");
        //	DCset=DCset.concat(htn.itemset);
        //	DCset=DCset.concat("]");
        break;
      case DS:
        state="DS" ;
        DSset=DSset.concat(htn.itemset);
        DSset=DSset.concat(",");
        //	DSset=DSset.concat("[");
        //	DSset=DSset.concat(htn.itemset);
        //	DSset=DSset.concat("]");
        break;
      case SC:
        state="SC" ;
        SCset=SCset.concat(htn.itemset);
        SCset=SCset.concat(",");
        //	SCset=SCset.concat("[");
        //	SCset=SCset.concat(htn.itemset);
        //	SCset=SCset.concat("]");
        break;
      case SS:
        state="SS" ;
        SSset=SSset.concat(htn.itemset);
        SSset=SSset.concat(",");
        //	SSset=SSset.concat("[");
        //	SSset=SSset.concat(htn.itemset);
        //	SSset=SSset.concat("]");
        break;
    }
//    System.out.print("Itemset:<"+htn.itemset+">");
//    System.out.print(" state:<"+state+">");
//    System.out.print(" counter:<"+htn.counter+">");
//    System.out.print(" starting:<"+htn.starting+">");
//    System.out.print(" startingk:<"+htn.startingk+">");
//    System.out.println(" needcounting:<"+htn.needcounting+">");

    if (htn.ht==null)
      return;
    for (int b=a+1;b<=itemsetsize(transa);b++)
      if (htn.ht.containsKey(getitemat(b,transa)))
        printhashtree((hashtreenode)htn.ht.get(getitemat(b,transa)),transa,b);
  }

//-------------------------------------------------------------
//  Method Name: transatrahashtree
//  Purpose    : recursive transaction traversal through hash tree
//  Parameter  : htn is a hashtreenode (when other method call this method,it is the root)
//             : transa : transaction
//             : a : recursive depth,only traversal itemset_part after a-th item
//             : e.g. tran="2 3 4 5", a=2, then, only "4 5" need traversal.
//  Return     :
//-------------------------------------------------------------
  public void transatrahashtree(String transa,hashtreenode htn,int a)
  {
    if (htn.needcounting)
      htn.counter++;
    if (htn.ht==null)
      return;
    else
      for (int b=a+1;b<=itemsetsize(transa);b++)
        if (htn.ht.containsKey(getitemat(b,transa)))
          transatrahashtree(transa,(hashtreenode)htn.ht.get(getitemat(b,transa)),b);
  }

//-------------------------------------------------------------
//  Method Name: checkcountedall
//  Purpose    : travese hashtree to check if an itemset has been counted
//             : through all transactions, stop counting it.
//  Parameter  : htn is a hashtreenode (when other method call this method,it is the root)
//             : transa : transaction
//             : startfrom : recursive depth
//  Return     :
//-------------------------------------------------------------
  public void checkcountedall(hashtreenode htn,String transa,int startfrom)
  {
    if ( htn.starting==tid && k!=htn.startingk )
    {
      if (htn.state==DS)
        htn.state=SS;
      else if (htn.state==DC)
        htn.state=SC;
      
      htn.needcounting=false;
    }

    if (htn.ht==null || htn.ht.isEmpty())
      return;

    for (int c=startfrom+1;c<=N;c++)
      if (htn.ht.containsKey(getitemat(c,transa)))
        checkcountedall((hashtreenode)htn.ht.get(getitemat(c,transa)),transa,c);
  }

//-------------------------------------------------------------
//  Method Name: checkcounter
//  Purpose    : ( DC==>DS )
//             : traverse hashtree to check if an itemset node is stated DC and
//             : it's counter>=minsup, then change its state to DS
//  Parameter  : htn is a hashtreenode (when other method call this method,it is the root)
//             : transa : transaction
//             : startfrom : recursive depth
//  Return     :
//-------------------------------------------------------------
  public void checkcounter(hashtreenode htn,String transa,int startfrom)
  {
    //if its a dashed circle and frequent, change its state to a dashed square
    if (htn.state==DC && htn.counter >= ((minsup*M)/100))
      htn.state=DS;

    if ( htn.ht.isEmpty())
      return;

    for (int c=startfrom+1;c<=N;c++)
      if (htn.ht.containsKey(getitemat(c,transa)))
      {
        checkcounter((hashtreenode)htn.ht.get(getitemat(c,transa)),transa,c);
      }
  }

//-------------------------------------------------------------
//  Method Name: checkhashtree
//  Purpose    : traverse hashtree
//             : for each of DS node's superset
//             : if all subset of it are SS/SC/DS
//             : generate new hashtreenode, and link it to its father
//             : e.g. tran="2 3 4 5", a=2, then, only "4 5" need traversal.
//  Parameter  : htn is a hashtreenode (when other method call this method,it is the root)
//             : transa : transaction
//             : transa: special transaction that includes all items
//             : startfrom : recursive depth, only check itemset_part after startfrom-th item
//  Return     :
//-------------------------------------------------------------
  public void checkhashtree(hashtreenode htn,String transa,int startfrom)
  {
    String superset=new String();
    String subset=new String();
    StringTokenizer stsuperset,stsubset;
    boolean dcfound;

    if ( htn.state==DS )
    {
      superset=gensuperset(htn.itemset);
      if (superset!=null)
      {
        stsuperset=new StringTokenizer(superset,",");
        while (stsuperset.hasMoreTokens())
        {
          String superset1=new String(stsuperset.nextToken());
          if (htn.ht.containsKey(getitemat(itemsetsize(superset1),superset1)))
            continue ;
          subset=gensubset(superset1);
          stsubset=new StringTokenizer(subset,",");
          dcfound=false;
          while (stsubset.hasMoreTokens())
            if (circlefound(root,stsubset.nextToken(),0))
            {
              dcfound=true;
              break;
            }
          if (!dcfound)
          {
            hashtreenode tmphtn=new hashtreenode(DC,superset1,0,tid,k);
            htn.ht.put(getitemat(itemsetsize(superset1),superset1),tmphtn);
          }
        }
      }
    }

    if (htn.ht==null || htn.ht.isEmpty())
      return;

    for (int c=startfrom+1;c<=N;c++)
      if (htn.ht.containsKey(getitemat(c,transa)))
        checkhashtree((hashtreenode)htn.ht.get(getitemat(c,transa)),transa,c);
  } //end public void checkhashtree(hashtreenode htn,String spetransa,int startfrom)

//-------------------------------------------------------------
//  Method Name: circlefound
//  Purpose    : called by checkhashtree
//  Parameter  : htn is a hashtreenode (when other method call this method,it is the root)
//             : transa : transaction
//             : transa: special transaction that includes all items
//             : startfrom : recursive depth, only check itemset_part after startfrom-th item
//  Return     :
//-------------------------------------------------------------
  public boolean circlefound(hashtreenode htn,String itemset,int startfrom) 
  {
    //if the node passed in is a dashed circle or a solid circle, return true
    if (htn.state==DC || htn.state==SC)
      return true;

    
    for (int c=startfrom+1;c<=itemsetsize(itemset);c++)
      if (htn.ht.containsKey(getitemat(c,itemset)))
        return circlefound((hashtreenode)htn.ht.get(getitemat(c,itemset)),itemset,c);

    return false;
  }

//-------------------------------------------------------------
//  Method Name: gensubset
//  Purpose    : generate all subset given an itemset
//  Parameter  : itemset
//  Return     : a string contains all subset deliminated by ","
//             : e.g. "1 2,1 3,2 3" is subset of "1 2 3"
//-------------------------------------------------------------
  public String gensubset(String itemset)
  {
    int len=itemsetsize(itemset);
    int j;
    String str1;
    String str2;
    String str3=new String();

    if (len==1)
      return null;
    for (int i=1;i<=len;i++)
    {
      StringTokenizer st=new StringTokenizer(itemset);
      str1=new String();
      for (j=1;j<i;j++)
      {
        str1=str1.concat(st.nextToken());
        str1=str1.concat(" ");
      }
      str2=st.nextToken();
      for (j=i+1;j<=len;j++)
      {
        str1=str1.concat(st.nextToken());
        str1=str1.concat(" ");
      }
      if (i!=1)
        str3=str3.concat(",");
      str3=str3.concat(str1.trim());
    }

    return str3;
  } //end public String gensubset(String itemset)

//-------------------------------------------------------------
//  Method Name: gensuperset
//  Purpose    : generate all superset given an itemset
//  Parameter  : itemset
//  Return     : a string contains all superset deliminated by ","
//             : e.g. "1 2,1 3,1 4" is superset of "1"
//-------------------------------------------------------------
  public String gensuperset(String itemset)
  {
    String str1,str2;
    int c;
    int i1=itemset.lastIndexOf(" ");

    if (i1==-1)
      str1=new String(itemset);
    else
      str1=new String(itemset.substring(i1+1));

    c=Integer.valueOf(str1).intValue();
    if (c==N)
      return null;
    else
    {
      str2=new String();
      for (int b=c+1;b<N;b++)
      {
        str2=str2.concat(itemset);
        str2=str2.concat(" ");
        str2=str2.concat(Integer.toString(b));
        str2=str2.concat(",");
      }
      str2=str2.concat(itemset);
      str2=str2.concat(" ");
      str2=str2.concat(Integer.toString(N));
    }
    return str2;
  }

//-------------------------------------------------------------
//  Method Name: dicProcess()
//  Purpose    : main processing method
//  Parameters :
//  Return     :
//-------------------------------------------------------------
  public dicProcess()  throws IOException
  {
    String fullitemset=new String();
    String transa=new String();
    String str0=new String();
    String oneline=new String();
    StringTokenizer st;
    FileInputStream file_in;
    BufferedReader data_in;
    Date d=new Date();
    int j;
    int lineprocessed=0;
    int numRead=0;
    boolean qiaole=false;
    long s1,s2;

    System.out.println("\nAlgorithm DIC starting now.....\n");
    getconfig();

    // generating 1-itemset in root.
    root=new hashtreenode(SS,null,0,1,0);
    for (int i=1;i<=N;i++)
    {
      String str=new String(Integer.toString(i));
      hashtreenode htn=new hashtreenode(DC,str,0,1,0);
      if (root.ht==null)
      {
        Hashtable<String, hashtreenode> ht=new Hashtable<String, hashtreenode>();
        root.ht=ht;
      }
      root.ht.put(str,htn);
    }

    file_in = new FileInputStream(transafile);
    data_in = new BufferedReader(new InputStreamReader(file_in));

    for (int i=1;i<=N;i++)
      fullitemset=fullitemset.concat(" " + Integer.toString(i));
    fullitemset = fullitemset.trim();

    k=0;
    tid=1;

    d=new Date();
    s1=d.getTime();
    System.out.print("Processing step M number: ");

    while (dashfound(root))
    {
      k++;
      System.out.print(k+"..");
     
      lineprocessed=0;
      while (lineprocessed<stepm)
      {
        oneline=data_in.readLine();
        numRead++;
        if ((oneline==null) || (numRead > M))
        {
          numRead=0;
          file_in = new FileInputStream(transafile);
          data_in = new BufferedReader(new InputStreamReader(file_in));
          tid=1;
          if (qiaole)
            oneline=data_in.readLine();
          else
            break;
        }
        
        st=new StringTokenizer(oneline.trim());
        j=0;
        transa=new String();
        while (st.hasMoreTokens())
        {
          j++;
          str0=st.nextToken();
          if (Integer.valueOf(str0).intValue() != 0)
            transa=transa.concat(" " + Integer.toString(j));
        }
        transa=transa.trim();
        transatrahashtree(transa,root,0);

        lineprocessed++;
        tid++;

        qiaole = (lineprocessed>=stepm && tid>M);

        if (tid>M)
          tid=1;
      }

//System.out.println("Now checking counter to find those whose counter>minsup...");
      checkcounter(root,fullitemset,0);
//System.out.println("Now checking hashtree to find those whose superset should be generated...");
      checkhashtree(root,fullitemset,0);
//System.out.println("Now checking those count all through and not need counting any more...");
      checkcountedall(root,fullitemset,0);
    }

    DSset=new String();
    DCset=new String();
    SCset=new String();
    SSset=new String();
    printhashtree(root,fullitemset,0);
    System.out.println("\n");

    StringTokenizer sss, tokenizedItemset;
    int i=1;
    boolean found=false, first=true;
    do
    {
        first=true;
        sss=new StringTokenizer(SSset,",");
        found=false;
        while (sss.hasMoreTokens())
        {
          String superset1=new String(sss.nextToken());
          tokenizedItemset = new StringTokenizer(superset1);
          if (tokenizedItemset.countTokens()==i)
          {
            if(first)
            {
                System.out.println("Frequent "+i+"-itemsets:");
                System.out.print("["+superset1);
                first=false;
                found=true;
            }
            else
                System.out.print(", "+superset1);
          }
        }
        if (found)
            System.out.println("]");
        
        i++;
    }while (found);

    d=new Date();
    s2=d.getTime();
    System.out.println("Execution time is: "+(s2-s1)/1000.0+" seconds.");
//System.out.println("End.");
  }
}