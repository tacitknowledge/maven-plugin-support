package com.tacitknowledge.pluginsupport.graphviz;

import org.codehaus.plexus.util.cli.*;
import org.apache.maven.plugin.logging.Log;

/**
 * Created by IntelliJ IDEA.
 * User: mshort
 * Date: Sep 22, 2006
 * Time: 6:29:25 PM
 */
public class GraphVizExecutable {

   public static final String LIB_BUILD_GRAPHVIZ_PATH = "dot";
   public static final String WINDOWS_EXTENSION = "";
   public static final String LINUX_EXTENSION = "";
//	public static final String MACOS_PATH = "/usr/local/graphviz-2.9/bin/dot";

   private static final String OS_KEY_WINDOWS = "Windows";
   private static final String OS_KEY_LINUX = "Linux";
   private static final String OS_KEY_MACOS = "Mac OS X";
   final String outGraph;
   Log log;
   private String inputFile;


   public GraphVizExecutable(Log log, String outGraph, String inputFile) {
      this(LIB_BUILD_GRAPHVIZ_PATH, log, outGraph, inputFile);
   }

   public GraphVizExecutable(String executable, Log log, String outGraph) {
      this(executable, log, outGraph, outGraph);
   }

   public GraphVizExecutable(Log log, String outGraph) {
      this(log, outGraph, outGraph);
   }

   public GraphVizExecutable(String executablePath, Log log, String outGraph, String inputFile) {
      this.log = log;
      this.outGraph = outGraph;
      this.inputFile = inputFile;

//      String executable;
     // Determine which 'dot' exectuble to use based on the current OS.
//        String os = System.getProperty("os.name");
//        if (os.contains(OS_KEY_WINDOWS)) {
//            executable = executablePath + WINDOWS_EXTENSION;
//        } else if (os.contains(OS_KEY_LINUX)) {
//            executable = executablePath + LINUX_EXTENSION;
//        } else {
//            log.warn("Unknown graphviz OS:" + os);
//			executable = null;
//		}
   }

   public void execute() {
      Commandline cmd = new Commandline();
      cmd.setExecutable("dot");

      try {
         // Checkstyle: MagicNumber off
         String[] arguments = new String[3];
         // Checkstyle: MagicNumber on
         String graph_format = "png";
         arguments[0] = "-T" + graph_format;
         arguments[1] = "-o" + outGraph + "." + graph_format;
         arguments[2] = inputFile;
         log.debug(" Graphviz dot args [" + arguments[0] + " " + arguments[1] + " " + arguments[2] + "]");
         cmd.addArguments(arguments);

         StreamConsumer consumer = new DefaultConsumer();

         int returnValue = CommandLineUtils.executeCommandLine(cmd,
            consumer, consumer);
         if (returnValue != 0) {
            log.info("Failed to generate visual graph from dependencies");
         }

      } catch (CommandLineException e) {
         log.warn("Failed to generate visual graph from dependencies", e);
      }
   }
}
