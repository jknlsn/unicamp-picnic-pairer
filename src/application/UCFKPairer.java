package application;

import java.util.*;
import java.io.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.jgrapht.alg.KuhnMunkresMinimalWeightBipartitePerfectMatching;
import org.jgrapht.alg.HopcroftKarpBipartiteMatching;

import com.opencsv.CSVReader;

public class UCFKPairer {

  private static int experience;
  private static boolean testmode = false;
  private static String maleFile, femaleFile, pairsFile, outFile, APIkey;
  private static SimpleWeightedGraph<Volunteer, DefaultWeightedEdge> graph;

  private static int maxpairs = 0;

  public UCFKPairer(int exp, boolean mode, String mfile, String ffile, String pfile, String ofile, String key){
	  experience = exp;
	  testmode = mode;
	  maleFile = mfile;
	  femaleFile = ffile;
	  pairsFile = pfile;
	  outFile = ofile;
	  APIkey = key;
	  System.out.println("pairsFile is : " + pairsFile);
	  init();
  }

  // Read in file given and initialise
  // public static void init(String maleFile, String femaleFile)
  public static void init()
  {

      graph = new SimpleWeightedGraph<Volunteer, DefaultWeightedEdge>(DefaultWeightedEdge.class);
      ArrayList<Volunteer> males = new ArrayList<Volunteer>();
      ArrayList<Volunteer> females = new ArrayList<Volunteer>();

      // Females read and add to ArrayList
      parseFiles(maleFile, males);
      parseFiles(femaleFile, females);

      // Create graph from information
      fillGraph(males, females);
  }

  public static void parseFiles(String filename, ArrayList<Volunteer> array)
  {
    String temp[];
    Volunteer volunteer;
    int exp;
    boolean driver;
    try {
      CSVReader reader = new CSVReader(new FileReader(filename), ',' , '"' , 1);
//    String[] nextLine;
      while ((temp = reader.readNext()) != null) {
         if (temp != null) {

          // Check if experienced, if picnics + camps is greater than EXPERIENCE THRESHOLD given
          exp = Integer.parseInt(temp[9]);

          // Check if can be a driver
          if (temp[7].equals("Yes") && temp[8].equals("Yes"))
          {
            driver = true;
          }
          else
          {
            driver = false;
          }

          volunteer = new Volunteer(temp[3], temp[5], temp[6], exp, driver, temp[12], Double.parseDouble(temp[9]), Double.parseDouble(temp[10]));
          System.err.println(temp[3]);
          array.add(volunteer);
      }

      }
      reader.close();
    } catch (Exception exception) {
//        exception.printStackTrace();
    }
  }

  public static void fillGraph(ArrayList<Volunteer> m, ArrayList<Volunteer> f)
  {

//    Haversine h = new Haversine();

    for (Volunteer girl : f)
    {
      graph.addVertex(girl);
    }

    // Populate graph for first run to determine number of pairings
    for (Volunteer boy : m)
    {
      graph.addVertex(boy);
      ArrayList<Volunteer> compatible = new ArrayList<Volunteer>();
      for (Volunteer girl : f)
      {
        // Check if compatible...
        // TODO: Check against last list of pairings
        if ( !boy.driver && !girl.driver )
        {
            continue;
        }
        else
        {
          //  TODO: Change top range from 10 to 5
          if ( boy.exp + girl.exp >= experience )//&& boy.exp + girl.exp <= 5)
          {
            // How to get edge weights
            compatible.add(girl);
            DefaultWeightedEdge e = graph.addEdge(boy, girl);
            graph.setEdgeWeight(e, 1);
          }
        }
      }

    }

    Set<Volunteer> p1 = new HashSet<Volunteer>(m);
    Set<Volunteer> p2 = new HashSet<Volunteer>(f);

    HopcroftKarpBipartiteMatching<Volunteer, DefaultWeightedEdge> alg =
        new HopcroftKarpBipartiteMatching<Volunteer, DefaultWeightedEdge>(graph, p1, p2);

    Set<DefaultWeightedEdge> match = alg.getMatching();
    System.err.println(match.size() + " PAIRS.");

    ArrayList<Volunteer> s = new ArrayList<Volunteer>();
    ArrayList<Volunteer> t = new ArrayList<Volunteer>();

    for (DefaultWeightedEdge pair : match)
    {
      s.add(graph.getEdgeSource(pair));
      t.add(graph.getEdgeTarget(pair));
      // System.out.println(graph.getEdgeSource(pair).name + " and " + graph.getEdgeTarget(pair).name);
      // System.out.println(graph.getEdgeSource(pair).name + " and " + graph.getEdgeTarget(pair).name + " are " + graph.getEdgeWeight(pair) + "kms away.");
    }

    // System.out.println(match);
    maxpairs = match.size();
    System.out.println(match.size() + " PAIRS.");

    // Fill in empty edges with large weighting
    graph = new SimpleWeightedGraph<Volunteer, DefaultWeightedEdge>(DefaultWeightedEdge.class);

    System.err.println(t.size() + " : " + s.size());

    for (Volunteer girl : t)
    {
      graph.addVertex(girl);
    }

    // Fill in empty edges with large weighting
    for (Volunteer boy : s)
    {
      graph.addVertex(boy);
      ArrayList<Volunteer> compatible = new ArrayList<Volunteer>();
      for (Volunteer girl : t)
      {
        // Check if compatible...
        // TODO: Check against last list of pairings
        if ( !boy.driver && !girl.driver )
        {
          DefaultWeightedEdge e = graph.addEdge(boy, girl);
          graph.setEdgeWeight(e, 10000);
        }
        else
        {
          //  TODO: Change top range from 10 to 5
          if ( boy.exp + girl.exp >= experience && boy.exp + girl.exp <= 5)
          {
            // How to get edge weights
            if (testmode){
              DefaultWeightedEdge e = graph.addEdge(boy, girl);
              graph.setEdgeWeight(e, 1);
            }
            compatible.add(girl);
          }
          else {
            DefaultWeightedEdge e = graph.addEdge(boy, girl);
            graph.setEdgeWeight(e, 10000);
          }
        }
      }

      if (!testmode){
        googleLookUp gl = new googleLookUp(APIkey);
        ArrayList <Double> times = gl.main(boy, compatible);

        int numCompatible = compatible.size();
        Volunteer girl;

        for (int i = 0; i < numCompatible; i++) {
          girl = compatible.get(i);
          DefaultWeightedEdge e = graph.addEdge(boy, girl);
          graph.setEdgeWeight(e, times.get(i));
        }
      }
    }

    // Find minimal matching
    KuhnMunkresMinimalWeightBipartitePerfectMatching<Volunteer, DefaultWeightedEdge> km =
      new KuhnMunkresMinimalWeightBipartitePerfectMatching<Volunteer, DefaultWeightedEdge>(graph, s, t);


    match = km.getMatching();
    String formatted = String.format("\n%-30s |   %-30s |  %9s |\n", "BOY", "GIRL", "TIME");
    System.out.println(formatted);

    if (!testmode){
    	PairPrinter pp = new PairPrinter(match, graph);
        pp.print(outFile);
    }

    formatted = String.format("\n%5.2fkm total travel distance.\n", km.getMatchingWeight());
    System.out.println(formatted);

  }

  public int maximumPairs()
  {
    return maxpairs;
  }

  public static void main(String[] args)
  {

    if (args.length < 3)
    {
      System.out.println("Please enter the filenames of the male and female data, and the experience threshold.");
      System.out.println("i.e.\njava UCFKPairer M.csv F.csv 3");
      return;
    }

    experience = Integer.parseInt(args[2]);
//    if (args.length >= 3){
//      testmode = Integer.parseInt(args[3]);
//    }

//    init(args[0], args[1]);

  }

}
