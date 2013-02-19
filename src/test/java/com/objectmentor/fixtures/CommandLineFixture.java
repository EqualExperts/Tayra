package com.objectmentor.fixtures;

import fit.*;

import java.io.*;
import java.util.*;

/**
 * LineGatherer is a Runnable that eats lines from a BufferedReader
 * and jams them into a queue.  It continues to read until the BufferedReader
 * returns EOF or error.
 */
class LineGatherer implements Runnable {
  private LinkedList<String> lines;
  private BufferedReader reader;
  private boolean done = false;

  public LineGatherer(BufferedReader reader) {
    this.lines = new LinkedList<String>();
    this.reader = reader;
  }

  public void run() {
    try {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.addLast(line);
      }
    } catch (IOException e) {
      lines.addLast("Exception:" + e.getMessage() + "\n");
    }
    done = true;
  }

  /**
   * readLine reads a line from the queue.  It will wait about a second for
   * a line to show up, otherwise it returns null.
   */
  public String readLine() throws Exception {
    for (int i = 0; i < 10 && lines.isEmpty() && !done; i++)
      Thread.sleep(100);
    if (lines.isEmpty())
      return null;
    else {
      String line = lines.removeFirst();
      if (CommandLineFixture.isVerbose) System.out.println("line = " + line);
      return line;
    }
  }

  public String[] getLines() {
    return (String[]) lines.toArray(new String[0]);
  }
}

/**
 * A FIT test fixture that provides shell-like behavior to a FIT table.
 * It allows you to run commands, and search their output.  It allows you
 * to invoke any shell command, either synchronously or asynchronously.  It allows
 * you to create and test files.
 *
 * The table rows are executed in order, from top to bottom.  At the end of the table any
 * processes that are still running are terminated and their stdout and stderr streams gathered
 * into the cell that specified their command.
 *
 * @author Robert C. Martin
 */

public class CommandLineFixture extends ActionFixture {
  /**
   * CommandProcess is record of the interesting variables that describe
   * a running process.
   */
  class CommandProcess {
    public LineGatherer stdoutGatherer;
    public LineGatherer stderrGatherer;
    public Parse commandCell;
    public Process process;
  }

  private HashMap<String, CommandProcess> commandProcessMap = new HashMap<String, CommandProcess>();

  static HashMap<String, String> definitions = new HashMap<String, String>();
  //Added by Dhaval
  static List<String> environment = new ArrayList<String>();
  static boolean isVerbose = false;

  /**
   * Private function -- ignore.
   */
  public void doTable(Parse table) {
    startTable();
    super.doTable(table);
    endTable();
  }

  private void startTable() {
  }

  private void endTable() {
    Set<String> processIds = commandProcessMap.keySet();
    for (Iterator<String> iterator = processIds.iterator(); iterator.hasNext();) {
      String processId = iterator.next();
      CommandProcess p = getCommandProcess(processId);
      flushProcess(p);
    }
  }

  private void flushProcess(CommandProcess p) {
    p.commandCell.at(1).addToBody("</pre>");
    flush(p.stdoutGatherer, p.commandCell);
    flush(p.stderrGatherer, p.commandCell);
    try {
      int status = p.process.exitValue();
      p.commandCell.addToBody("<hr/>" + label("terminated with exit value " + status));
    } catch (Exception e) {
      terminateProcess(p);
      p.commandCell.addToBody("<hr/>" + label("forcibly terminated"));
    }
  }

  private void terminateProcess(CommandProcess p) {
    p.process.destroy();
    try {
      p.process.waitFor();
    } catch (InterruptedException e1) {
    }
  }

  /**
   * Starts a command but does not wait for it to finish.  The stdout and stderr
   * streams are captured and accessible to the 'find' and 'contains' commands.
   * @param   command   The command to execute
   * @param   processId An identifier for the process.  This identifier can be used with other
   *                    directives such as find, contains, and waitFor to reference the process
   *                    and the stdout and stderr streams.  This field can be left blank.
   */
  public void spawn() throws Exception {
    Parse commandCell = cells.at(1);
    Parse processIdCell = cells.at(2);
    doSpawn(commandCell, processIdCell);
  }

  private CommandProcess doSpawn(Parse commandCell, Parse processIdCell) throws IOException {
    String command = replaceDefinitions(commandCell.text());
    String processId = processIdCell.text();

    if (isBlank(processId))
      processId = generateUniqueProcessId();

    if (!processIdAlreadyInUse(processId)) {
      CommandProcess p = new CommandProcess();
      p.process = execute(command);
      p.stdoutGatherer = makeGatherer(p.process.getInputStream());
      p.stderrGatherer = makeGatherer(p.process.getErrorStream());
      p.commandCell = cells.at(1);
      commandProcessMap.put(processId, p);
      commandCell.addToBody("<hr><pre>");
      return p;
    } else {
      processIdCell.addToBody("<hr/>" + label("duplicate process id ignored"));
      wrong(processIdCell);
    }
    return null;
  }

  /**
   * Wait for a spawned process to complete.
   * @param processId The processId of the process to wait for.
   */
  public void waitFor() {
    Parse processIdCell = cells.at(1);
    try {
      doWaitFor(processIdCell);
    } catch (Exception e) {
      exception(processIdCell, e);
    }
  }

  private void doWaitFor(Parse processIdCell) throws Exception {
    String processId = processIdCell.text();
    if (checkValidProcess(processId, processIdCell)) {
      CommandProcess p = getCommandProcess(processId);
      p.process.waitFor();
    }
  }

  /**
   * Execute and wait for a command to complete.  This command has the same
   * syntax as spawn, except that it pauses execution of the table in order to
   * wait for the command to complete.
   * @see spawn()
   */
  public void command() {
    Parse commandCell = cells.at(1);
    Parse processIdCell = cells.at(2);
    try {
      CommandProcess p = doSpawn(commandCell, processIdCell);
      if (p != null) p.process.waitFor();
    } catch (Exception e) {
      exception(commandCell, e);
    }
  }

  /**
   * pauses execution of the table for the specified number of seconds
   * @param seconds the number of seconds to pause the table.
   */
  public void pause() throws Exception {
    String seconds = cells.at(1).text();
    int milliseconds = 1000 * Integer.parseInt(seconds);
    Thread.sleep(milliseconds);
  }

  private LineGatherer makeGatherer(InputStream s) {
    LineGatherer gatherer = new LineGatherer(new BufferedReader(new InputStreamReader(s)));
    new Thread(gatherer).start();
    return gatherer;
  }

  private Process execute(String command) throws IOException {
    if (isVerbose) System.out.println("command = " + command);
    String[] env = getEnvars();
    /* Parameterized env: Dhaval */
	Process p = Runtime.getRuntime().exec(command, env);
    return p;
  }

  /* Added By Dhaval */
  private String[] getEnvars() {
	return (String[]) environment.toArray(new String[0]);
  }

private boolean processIdAlreadyInUse(String processId) {
    return commandProcessMap.containsKey(processId);
  }

  private String generateUniqueProcessId() {
    String processId;
    processId = "blank" + Math.random();
    return processId;
  }

  private boolean isBlank(String processId) {
    return processId.trim().equals("");
  }

  private boolean checkValidProcess(String processId, Parse cell) {
    if (!processIdAlreadyInUse(processId)) {
      cell.addToBody("<hr/>" + label(processId + " is not a valid process id."));
      wrong(cell);
      return false;
    } else
      return true;
  }

  /**
   * searches the specified stream for the specified string.  The stream is searched line
   * by line.  The search stops at the first line that contains the specified target string.
   * All previous lines are copied into the command cell and then discarded.  They will not
   * be searched again, either by find, or by contains.  If the output stream is empty, the
   * search waits for about a second for some output and then gives up.
   *
   * Use find when order is important.  Use it to eliminate early output and restrict searches
   * by find and contains to subsequent output.
   *
   * @see contains()
   *
   * @param   stream    The stream to search.  This stream is specified with the syntax
   *                    processId.streamId.  The processId is from the spawn or command
   *                    method.  The streamId is either 'stdout' or 'stderr'.  Thus
   *                    'server.stdout' is the streamId for stdout of the server process.
   *
   * @param   target    The string to search for.  No regular expressions are used right now.
   */
  public void find() throws Exception {
    Parse processIdCell = cells.at(1);
    Parse targetCell = cells.at(2);

    String streamExpression = processIdCell.text();
    String target = targetCell.text();
    String tokens[] = parseStreamExpression(streamExpression, processIdCell);
    if (tokens != null) {
      String processId = tokens[0];
      String streamId = tokens[1];
      CommandProcess p = getCommandProcess(processId);
      if (streamId.equals("stdout")) {
        find(p.stdoutGatherer, target, p.commandCell);
      } else if (streamId.equals("stderr")) {
        find(p.stderrGatherer, target, p.commandCell);
      } else
        streamExpressionError("Stream id not 'stdout' or 'stderr'");
    }
  }

  /**
   * searches the existing output of the specified stream for the specified
   * target string.  The search does not wait for more output.  Nor does it
   * discard any lines in the existing stream.  Two contains directives can
   * be specified in any order and they will succeed so long as the target
   * string is in the current output stream.  However, any lines discareded
   * by the find directive will not be searched.
   *
   * @see find()
   *
   * @param streamId    See find().
   * @param target      See find().
   */
  public void contains() throws Exception {
    Parse processIdCell = cells.at(1);
    Parse targetCell = cells.at(2);

    String streamExpression = processIdCell.text();
    String target = targetCell.text();
    String tokens[] = parseStreamExpression(streamExpression, processIdCell);
    if (tokens != null) {
      String processId = tokens[0];
      String streamId = tokens[1];
      CommandProcess p = getCommandProcess(processId);
      if (streamId.equals("stdout")) {
        contains(p.stdoutGatherer.getLines(), target, p.commandCell);
      } else if (streamId.equals("stderr")) {
        contains(p.stderrGatherer.getLines(), target, p.commandCell);
      } else
        streamExpressionError("Stream id not 'stdout' or 'stderr'");
    }
  }

  private String[] parseStreamExpression(String streamExpression, Parse processIdCell) {
    String[] tokens;
    tokens = null;
    int dotIndex = streamExpression.indexOf('.');
    if (dotIndex == 0) {
      streamExpressionError("No 'dot' present in expression.");
    } else {
      String processId = streamExpression.substring(0, dotIndex);
      String streamId = streamExpression.substring(dotIndex + 1);

      if (streamId.length() == 0) {
        streamExpressionError("No stream id.");
      } else if (!checkValidProcess(processId, processIdCell)) {
        streamExpressionError(processId + " is not a valid processId.");
      } else
        tokens = new String[]{processId, streamId};
    }
    return tokens;
  }

  private CommandProcess getCommandProcess(String processId) {
    CommandProcess p = commandProcessMap.get(processId);
    return p;
  }

  private void find(LineGatherer gatherer, String target, Parse commandCell) throws Exception {
    String line;
    while ((line = gatherer.readLine()) != null) {
      commandCell.addToBody(line + "\n");
      if (line.indexOf(target) >= 0) {
        right(cells.at(2));
        commandCell.addToBody(label("found " + target) + "<br>");
        return;
      }
    }
    Parse wrongCell = cells.at(2);
    wrong(wrongCell);
    commandCell.addToBody(label("gave up finding " + target) + "<br>");
  }

  private void contains(String[] lines, String target, Parse commandCell) throws Exception {
    Thread.yield(); // let the gatherers gather.
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i];
      if (line.indexOf(target) >= 0) {
        right(cells.at(2));
        return;
      }
    }
    wrong(cells.at(2));
  }

  private void streamExpressionError(String error) {
    Parse wrongCell = cells.at(1);
    wrong(wrongCell);
    wrongCell.addToBody("<hr/>" + label("Syntax Error: " + error + " Should be processId.stderr or processId.stdout"));
  }


  private void flush(LineGatherer gatherer, Parse commandCell) {
    String line;
    try {
      while ((line = gatherer.readLine()) != null) {
        commandCell.addToBody(line + "\n");
      }
    } catch (Exception e) {
      exception(commandCell, e);
    }
  }

  /**
   * creates a macro that will be replaced in all command and spawn commands.
   * These macros are recursive, so any macro that appears in another macro
   * will be replaced properly.
   *
   * @param name    The name of the macro.  My convention has been to use all
   *                all uppercase for macro names, just as in C.
   *
   * @param contents  The string that will replace the macro.  This string can
   *                  contain other macros.
   */
  public void definition() {
    Parse nameCell = cells.at(1);
    Parse contentCell = cells.at(2);

    String name = nameCell.text().trim();
    String contents = contentCell.text();

    if (name.length() == 0)
      nameCell.addToBody(label("you have to put a name here."));
    else
      doDefinition(name, contents);
  }

  void doDefinition(String name, String contents) {
    definitions.put(name, contents);
  }

  String replaceDefinitions(String source) {
    String target = new String(source);
    Set<String> macros = definitions.keySet();
    for (Iterator<String> i = macros.iterator(); i.hasNext();) {
      String name = (String) i.next();
      String content = (String) definitions.get(name);
      int pos;
      while ((pos = target.indexOf(name)) >= 0) {
        target = target.substring(0, pos) + content + target.substring(pos + name.length());
      }
    }
    if (source.equals(target))
      return target;
    else
      return replaceDefinitions(target);
  }
  /* Added by Dhaval */
  public void environment() {
	    Parse nameCell = cells.at(1);
	    Parse contentCell = cells.at(2);

	    String name = nameCell.text().trim();
	    String contents = contentCell.text();

	    if (name.length() == 0)
	      nameCell.addToBody(label("you have to put a name here."));
	    else
	      doEnvironment(name, contents);
  }

  /* Added by Dhaval */
  void doEnvironment(String name, String contents) {
	    environment.add(name + "=" + contents);
  }
 
  /**
   * prints a message on stdout of the FIT runner.  Used to show progress in long
   * test runs.
   *
   * @param string  The string to print on stdout.
   */
  public void title() {
    Parse titleCell = cells.at(1);
    System.out.println(".." + titleCell.text());
  }

  /**
   * Creates a file with the specified name and the specified contents.  The file is created
   * in the directory that the FIT runner is running in.
   *
   * @param filename    The name of the file to create.  This can be a pathname.
   *
   * @param contents    This string, which can contain many lines, is written to the file.
   *                    Currently this operation unescapes &amp&quot; sequences into double quotes.
   */
  public void createFile() {
    Parse filenameCell = cells.at(1);
    Parse fileContentsCell = cells.at(2);
    String filename = filenameCell.text().trim();
    String contents = unescape(fileContentsCell.text(), "&quot;", "\"");;
    if (checkFilename(filename, filenameCell)) {
      try {
        File file = new File(filename);
        FileWriter writer = new FileWriter(file);
        writer.write(contents);
        writer.close();
      } catch (IOException e) {
        exception(filenameCell, e);
      }
    }
  }

  private boolean checkFilename(String filename, Parse filenameCell) {
    if (filename.length() == 0) {
      filenameCell.addToBody(label("you have to put a filename here."));
      wrong(filenameCell);
      return false;
    } else {
      return true;
    }
  }

  /**
   * checks to see if a file exists.
   *
   * @param filename  The name of the file to check.  This can be a path.
   */
  public void fileExists() {
    Parse filenameCell = cells.at(1);
    String filename = filenameCell.text().trim();
    if (checkFilename(filename, filenameCell)) {
      File file = new File(filename);
      if (file.exists())
        right(filenameCell);
      else
        wrong(filenameCell);
    }
  }

  static String unescape(String string, String from, String to) {
    String target = new String(string);
    int pos;
    while ((pos = target.indexOf(from)) >= 0) {
      target = target.substring(0,pos) + to + target.substring(pos+from.length());
    }
    return target;
  }

  /**
   * turns on verbose output.  All stdout and stderr from all commands and spawns are
   * sent to the stdout of the FIT runner.  This can be volumnous, so beware.
   * @see verboseOff()
   */
  public void verbose() {
    isVerbose = true;
  }

  /**
   * turns verbose output off.
   * @see verbose()
   */
  public void verboseOff() {
    isVerbose = false;
  }
}