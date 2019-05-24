package app.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

import app.graph.structure.ColorShape;
import app.graph.structure.Edge;
import app.graph.structure.Fill;
import app.graph.structure.Graph;
import app.graph.structure.PositionShape;
import app.graph.structure.Stroke;
import app.graph.structure.Vertex;
import app.gui.main.Constants;

/**
 * <i>Input</i>. This class provides methods for reading strings and numbers
 * from standard input, file input, URLs, and sockets.
 * <p>
 * The Locale used is: language = English, country = US. This is consistent with
 * the formatting conventions with Java floating-point literals, command-line
 * arguments (via {@link Double#parseDouble(String)}) and standard output.
 * <p>
 * For additional documentation, see
 * <a href="http://introcs.cs.princeton.edu/31datatype">Section 3.1</a> of
 * <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by
 * Robert Sedgewick and Kevin Wayne.
 * <p>
 * Like {@link Scanner}, reading a token also consumes preceding Java
 * whitespace, reading a full line consumes the following end-of-line delimeter,
 * while reading a character consumes nothing extra.
 * <p>
 * Whitespace is defined in {@link Character#isWhitespace(char)}. Newlines
 * consist of \n, \r, \r\n, and Unicode hex code points 0x2028, 0x2029, 0x0085;
 * see <tt><a href="http://www.docjar.com/html/api/java/util/Scanner.java.html">
 *  Scanner.java</a></tt> (NB: Java 6u23 and earlier uses only \r, \r, \r\n).
 *
 * @author David Pritchard
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
public final class In {

	///// begin: section (1 of 2) of code duplicated from In to StdIn.

	// assume Unicode UTF-8 encoding
	private static final String CHARSET_NAME = "UTF-8";

	// assume language = English, country = US for consistency with System.out.
	private static final Locale LOCALE = Locale.US;

	// the default token separator; we maintain the invariant that this value
	// is held by the scanner's delimiter between calls
	private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\p{javaWhitespace}+");

	// makes whitespace characters significant
	private static final Pattern EMPTY_PATTERN = Pattern.compile("");

	// used to read the entire input. source:
	// http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
	private static final Pattern EVERYTHING_PATTERN = Pattern.compile("\\A");

	//// end: section (1 of 2) of code duplicated from In to StdIn.

	private Scanner scanner;
	//
	private static final Pattern NODE_PATTERN = Pattern
			.compile("(node.\\[)(.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*)");

	private static final Pattern EDGE_PATTERN = Pattern.compile("(edge.\\[)(.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*\\s*.*)");

	/**
	 * Initializes an input stream from standard input.
	 */
	public In() {
		scanner = new Scanner(new BufferedInputStream(System.in), CHARSET_NAME);
		scanner.useLocale(LOCALE);
	}

	/**
	 * Initializes an input stream from a socket.
	 *
	 * @param socket
	 *            the socket
	 * @throws IllegalArgumentException
	 *             if cannot open {@code socket}
	 * @throws NullPointerException
	 *             if {@code socket} is {@code null}
	 */
	public In(Socket socket) {
		if (socket == null)
			throw new NullPointerException("argument is null");
		try {
			InputStream is = socket.getInputStream();
			scanner = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
			scanner.useLocale(LOCALE);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Could not open " + socket);
		}
	}

	/**
	 * Initializes an input stream from a URL.
	 *
	 * @param url
	 *            the URL
	 * @throws IllegalArgumentException
	 *             if cannot open {@code url}
	 * @throws NullPointerException
	 *             if {@code url} is {@code null}
	 */
	public In(URL url) {
		if (url == null)
			throw new NullPointerException("argument is null");
		try {
			URLConnection site = url.openConnection();
			InputStream is = site.getInputStream();
			scanner = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
			scanner.useLocale(LOCALE);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Could not open " + url);
		}
	}

	/**
	 * Initializes an input stream from a file.
	 *
	 * @param file
	 *            the file
	 * @throws IllegalArgumentException
	 *             if cannot open {@code file}
	 * @throws NullPointerException
	 *             if {@code file} is {@code null}
	 */
	public In(File file) {
		if (file == null)
			throw new NullPointerException("argument is null");
		try {
			scanner = new Scanner(file, CHARSET_NAME);
			scanner.useLocale(LOCALE);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Could not open " + file);
		}
	}

	/**
	 * Initializes an input stream from a filename or web page name.
	 *
	 * @param name
	 *            the filename or web page name
	 * @throws IllegalArgumentException
	 *             if cannot open {@code name} as a file or URL
	 * @throws NullPointerException
	 *             if {@code name} is {@code null}
	 */
	public In(String name) {
		if (name == null)
			throw new NullPointerException("argument is null");
		try {
			// first try to read file from local file system
			File file = new File(name);
			if (file.exists()) {
				scanner = new Scanner(file, CHARSET_NAME);
				scanner.useLocale(LOCALE);
				return;
			}

			// next try for files included in jar
			URL url = getClass().getResource(name);

			// or URL from web
			if (url == null) {
				url = new URL(name);
			}

			URLConnection site = url.openConnection();

			// in order to set User-Agent, replace above line with these two
			// HttpURLConnection site = (HttpURLConnection)
			// url.openConnection();
			// site.addRequestProperty("User-Agent", "Mozilla/4.76");

			InputStream is = site.getInputStream();
			scanner = new Scanner(new BufferedInputStream(is), CHARSET_NAME);
			scanner.useLocale(LOCALE);
		} catch (IOException ioe) {
			throw new IllegalArgumentException("Could not open " + name);
		}
	}

	/**
	 * Initializes an input stream from a given {@link Scanner} source; use with
	 * <tt>new Scanner(String)</tt> to read from a string.
	 * <p>
	 * Note that this does not create a defensive copy, so the scanner will be
	 * mutated as you read on.
	 *
	 * @param scanner
	 *            the scanner
	 * @throws NullPointerException
	 *             if {@code scanner} is {@code null}
	 */
	public In(Scanner scanner) {
		if (scanner == null)
			throw new NullPointerException("argument is null");
		this.scanner = scanner;
	}

	/**
	 * Returns true if this input stream exists.
	 *
	 * @return <tt>true</tt> if this input stream exists; <tt>false</tt>
	 *         otherwise
	 */
	public boolean exists() {
		return scanner != null;
	}

	//// begin: section (2 of 2) of code duplicated from In to StdIn,
	//// with all methods changed from "public" to "public static".

	/**
	 * Returns true if input stream is empty (except possibly whitespace). Use
	 * this to know whether the next call to {@link #readString()},
	 * {@link #readDouble()}, etc will succeed.
	 *
	 * @return <tt>true</tt> if this input stream is empty (except possibly
	 *         whitespace); <tt>false</tt> otherwise
	 */
	public boolean isEmpty() {
		return !scanner.hasNext();
	}

	/**
	 * Returns true if this input stream has a next line. Use this method to
	 * know whether the next call to {@link #readLine()} will succeed. This
	 * method is functionally equivalent to {@link #hasNextChar()}.
	 *
	 * @return <tt>true</tt> if this input stream is empty; <tt>false</tt>
	 *         otherwise
	 */
	public boolean hasNextLine() {
		return scanner.hasNextLine();
	}

	/**
	 * Returns true if this input stream has more inputy (including whitespace).
	 * Use this method to know whether the next call to {@link #readChar()} will
	 * succeed. This method is functionally equivalent to {@link #hasNextLine()}
	 * .
	 * 
	 * @return <tt>true</tt> if this input stream has more input (including
	 *         whitespace); <tt>false</tt> otherwise
	 */
	public boolean hasNextChar() {
		scanner.useDelimiter(EMPTY_PATTERN);
		boolean result = scanner.hasNext();
		scanner.useDelimiter(WHITESPACE_PATTERN);
		return result;
	}

	/**
	 * Reads and returns the next line in this input stream.
	 *
	 * @return the next line in this input stream; <tt>null</tt> if no such line
	 */
	public String readLine() {
		String line;
		try {
			line = scanner.nextLine();
		} catch (NoSuchElementException e) {
			line = null;
		}
		return line;
	}

	/**
	 * Reads and returns the next character in this input stream.
	 *
	 * @return the next character in this input stream
	 */
	public char readChar() {
		scanner.useDelimiter(EMPTY_PATTERN);
		String ch = scanner.next();
		assert ch.length() == 1 : "Internal (Std)In.readChar() error!" + " Please contact the authors.";
		scanner.useDelimiter(WHITESPACE_PATTERN);
		return ch.charAt(0);
	}

	/**
	 * Reads and returns the remainder of this input stream, as a string.
	 *
	 * @return the remainder of this input stream, as a string
	 */
	public String readAll() {
		if (!scanner.hasNextLine())
			return "";

		String result = scanner.useDelimiter(EVERYTHING_PATTERN).next();
		// not that important to reset delimeter, since now scanner is empty
		scanner.useDelimiter(WHITESPACE_PATTERN); // but let's do it anyway
		return result;
	}

	/**
	 * Reads the next token from this input stream and returns it as a
	 * <tt>String</tt>.
	 *
	 * @return the next <tt>String</tt> in this input stream
	 */
	public String readString() {
		return scanner.next();
	}

	/**
	 * Reads the next token from this input stream, parses it as a <tt>int</tt>,
	 * and returns the <tt>int</tt>.
	 *
	 * @return the next <tt>int</tt> in this input stream
	 */
	public int readInt() {
		return scanner.nextInt();
	}

	/**
	 * Reads the next token from this input stream, parses it as a
	 * <tt>double</tt>, and returns the <tt>double</tt>.
	 *
	 * @return the next <tt>double</tt> in this input stream
	 */
	public double readDouble() {
		return scanner.nextDouble();
	}

	/**
	 * Reads the next token from this input stream, parses it as a
	 * <tt>float</tt>, and returns the <tt>float</tt>.
	 *
	 * @return the next <tt>float</tt> in this input stream
	 */
	public float readFloat() {
		return scanner.nextFloat();
	}

	/**
	 * Reads the next token from this input stream, parses it as a <tt>long</tt>
	 * , and returns the <tt>long</tt>.
	 *
	 * @return the next <tt>long</tt> in this input stream
	 */
	public long readLong() {
		return scanner.nextLong();
	}

	/**
	 * Reads the next token from this input stream, parses it as a
	 * <tt>short</tt>, and returns the <tt>short</tt>.
	 *
	 * @return the next <tt>short</tt> in this input stream
	 */
	public short readShort() {
		return scanner.nextShort();
	}

	/**
	 * Reads the next token from this input stream, parses it as a <tt>byte</tt>
	 * , and returns the <tt>byte</tt>.
	 * <p>
	 * To read binary data, use {@link BinaryIn}.
	 *
	 * @return the next <tt>byte</tt> in this input stream
	 */
	public byte readByte() {
		return scanner.nextByte();
	}

	/**
	 * Reads the next token from this input stream, parses it as a
	 * <tt>boolean</tt> (interpreting either <tt>"true"</tt> or <tt>"1"</tt> as
	 * <tt>true</tt>, and either <tt>"false"</tt> or <tt>"0"</tt> as
	 * <tt>false</tt>).
	 *
	 * @return the next <tt>boolean</tt> in this input stream
	 */
	public boolean readBoolean() {
		String s = readString();
		if (s.equalsIgnoreCase("true"))
			return true;
		if (s.equalsIgnoreCase("false"))
			return false;
		if (s.equals("1"))
			return true;
		if (s.equals("0"))
			return false;
		throw new InputMismatchException();
	}

	/**
	 * Reads all remaining tokens from this input stream and returns them as an
	 * array of strings.
	 *
	 * @return all remaining tokens in this input stream, as an array of strings
	 */
	public String[] readAllStrings() {
		// we could use readAll.trim().split(), but that's not consistent
		// since trim() uses characters 0x00..0x20 as whitespace
		String[] tokens = WHITESPACE_PATTERN.split(readAll());
		if (tokens.length == 0 || tokens[0].length() > 0)
			return tokens;
		String[] decapitokens = new String[tokens.length - 1];
		for (int i = 0; i < tokens.length - 1; i++)
			decapitokens[i] = tokens[i + 1];
		return decapitokens;
	}

	/**
	 * Reads all remaining lines from this input stream and returns them as an
	 * array of strings.
	 *
	 * @return all remaining lines in this input stream, as an array of strings
	 */
	public String[] readAllLines() {
		ArrayList<String> lines = new ArrayList<String>();
		while (hasNextLine()) {
			lines.add(readLine());
		}
		return lines.toArray(new String[0]);
	}

	/**
	 * Reads all remaining tokens from this input stream, parses them as
	 * integers, and returns them as an array of integers.
	 *
	 * @return all remaining lines in this input stream, as an array of integers
	 */
	public int[] readAllInts() {
		String[] fields = readAllStrings();
		int[] vals = new int[fields.length];
		for (int i = 0; i < fields.length; i++)
			vals[i] = Integer.parseInt(fields[i]);
		return vals;
	}

	/**
	 * Reads all remaining tokens from this input stream, parses them as
	 * doubles, and returns them as an array of doubles.
	 *
	 * @return all remaining lines in this input stream, as an array of doubles
	 */
	public double[] readAllDoubles() {
		String[] fields = readAllStrings();
		double[] vals = new double[fields.length];
		for (int i = 0; i < fields.length; i++)
			vals[i] = Double.parseDouble(fields[i]);
		return vals;
	}

	///// end: section (2 of 2) of code duplicated from In to StdIn */

	/**
	 * Closes this input stream.
	 */
	public void close() {
		scanner.close();
	}

	// public boolean isNodesInLine(Integer[] ids, String line) {
	// for (int i = 0; i < ids.length; i++) {
	// if (!isNodeInLine(ids[i], line)) {
	// return false;
	// }
	// }
	// return true;
	// }
	//
	// public boolean isNodeInLine(int idNode, String line) {
	// String pattern = "((^|\\s)(" + idNode + ")(\\s|$))";
	// Pattern patternId = Pattern.compile(pattern);
	// Matcher m = patternId.matcher(line);
	// while (m.find()) {
	// return true;
	// }
	// return false;
	// }

	/**
	 * Method to count number of lines where this idNode appears into toRead
	 * String
	 * 
	 * @param idNode
	 *            IdNode
	 * @param toRead
	 *            String where this IdNode going to search
	 * @return The number of lines where idNode appears
	 */
	// public int numberNodeInLines(int idNode, String toRead) {
	// String pattern = "((^|\\s)(" + idNode + ")(\\s|$))";
	// Pattern patternId = Pattern.compile(pattern);
	// Matcher m = patternId.matcher(toRead);
	// int count = 0;
	// while (m.find()) {
	// count++;
	// }
	// return count;
	// }

	/**
	 * @param toRead
	 * @return
	 */
	// public List<Integer> getAllNodesInLine(String toRead) {
	// String pattern = "(\\d+)";
	// Pattern patternId = Pattern.compile(pattern);
	// Matcher m = patternId.matcher(toRead);
	//
	// List<Integer> group = new ArrayList<Integer>();
	// while (m.find()) {
	// group.add(Integer.parseInt(m.group(0)));
	// }
	// return group;
	// }

	/**
	 * Get all the lines into the file where ID appears
	 * 
	 * @param id
	 * @param toRead
	 * @return
	 */
	// public String[] getLineById(int id, String toRead) {
	//
	// System.out.println("toREAD : " + toRead);
	//
	// String pattern = "((^|\\s)(" + id + ")(\\s|$))";
	// Pattern patternId = Pattern.compile(pattern);
	// Matcher m = patternId.matcher(toRead);
	//
	// List<String> group = new ArrayList<String>();
	// while (m.find()) {
	// group.add(m.group(0));
	// }
	//
	// String[] result = new String[group.size()];
	// for (int i = 0; i < group.size(); i++) {
	// result[i] = group.get(i);
	// }
	//
	// return result;
	// }

	/**
	 * Read a line in a SUMGRA format file
	 * 
	 * @param sumgraLines
	 * @return
	 */
	public Graph graphSumgra(String sumgraLines) {
		Matcher m;
		Matcher mSource, mTarget;
		Matcher mEdgesType;
		Pattern sumgraLine = Pattern.compile("\\d*\\s+\\d*\\s+\\S+\\n");
		Pattern nodeList = Pattern.compile("(^\\d*)(\\s+)(\\d*)");
		Pattern edgeTypeList = Pattern.compile("(^\\d*)(\\s+)(\\d*)(\\s+)(\\S+)");

		int nodeSourceId = 0;
		int nodeTargetId = 0;
		Graph g = new Graph();

		PositionShape position = new PositionShape(0, 0);
		Fill fillNode = null;
		Stroke strokeNode = null;
		int typeNode = Constants.GRAPH_DB_NODE_TYPE;
		boolean isLabelledNode = false;
		boolean isVisibleNode = true;
		double radius = 15;

		m = sumgraLine.matcher(sumgraLines);
		while (m.find()) {
			// node source
			mSource = nodeList.matcher(m.group());
			if (mSource.find()) {
				nodeSourceId = Integer.parseInt(mSource.group(1));
			}
			Vertex nodeS = new Vertex(nodeSourceId, "label " + nodeSourceId, typeNode, position, strokeNode, fillNode,
					isLabelledNode, isVisibleNode, radius);
			if (!g.getListNode().contains(nodeS)) {
				g.getListNode().add(nodeS);
			}

			// node target
			mTarget = nodeList.matcher(m.group());
			if (mTarget.find()) {
				nodeTargetId = Integer.parseInt(mTarget.group(3));
			}
			Vertex nodeT = new Vertex(nodeTargetId, "label " + nodeTargetId, typeNode, position, strokeNode, fillNode,
					isLabelledNode, isVisibleNode, radius);
			if (!g.getListNode().contains(nodeT)) {
				g.getListNode().add(nodeT);
			}

			mEdgesType = edgeTypeList.matcher(m.group());
			if (mEdgesType.find()) {
				String[] edgesType = mEdgesType.group(5).split(",");
				for (int i = 0; i < edgesType.length; i++) {
					int typeEdge = Integer.parseInt(edgesType[i]);
					boolean isLabelledEdge = false;
					boolean isVisible = false;
					double distance = 0.0d;
					String labelEdge = "Edge from node " + nodeSourceId + " to node " + nodeTargetId;
					int idEdge = g.getListEdge().size();
					Edge e = new Edge(idEdge, labelEdge, typeEdge, position, null, null, isLabelledEdge, isVisible,
							nodeSourceId, nodeTargetId, distance);
					if (!g.getListEdge().contains(e)) {
						g.getListEdge().add(e);
					}
				}
			}

		}

		return g;
	}

	/**
	 * 
	 * Method to get all nodes of a text line
	 * 
	 * @param nodeString
	 * @return a List of vertices
	 */
	public List<Vertex> getNodes(String nodeString) {
		// public IntObjectHashMap<Vertex> getNodes(String nodeString) {
		Matcher m;
		Matcher x;
		m = NODE_PATTERN.matcher(nodeString);
		Pattern pId = Pattern.compile("(id.)(\\d*)");
		Pattern pWeight = Pattern.compile("(weight.)(\\d*)");
		Pattern pLabel = Pattern.compile("(label.)(\")(.*)(\")");
		// Pattern pPin = Pattern.compile("(pin.)(\\d*)");
		Pattern pXPosition = Pattern.compile("(graphics.*\\s*.*\\s.*x\\s)(\\d.*)");
		Pattern pYPosition = Pattern.compile("(graphics.*\\s*.*\\s.*\\s*y\\s)(\\d.*)");
		Pattern pSize = Pattern.compile("(graphics.*\\s*.*\\s.*\\s.*\\s*w*\\s)(\\d.*)");
		// Pattern pXPosition = Pattern.compile("(x.)(\\d.*)");
		// Pattern pYPosition = Pattern.compile("(graphics [.)(y.)(\\d.*)(])");
		// Pattern pSize = Pattern.compile("(w.)(\\d.*)");

		int id;
		String label;
		// int type = 0;
		double positionX;
		double positionY;
		double diameter;
		double weight;
		List<Vertex> listNodes = new ArrayList<Vertex>();

		while (m.find()) {
			label = null;
			id = 0;
			positionX = 0;
			positionY = 0;
			diameter = 0;
			weight = 0;
			x = pId.matcher(m.group(2));
			if (x.find()) {
				id = Integer.parseInt(x.group(2));
			}
			x = pWeight.matcher(m.group(2));
			if (x.find()) {
				weight = Float.parseFloat(x.group(2));
			}
			x = pLabel.matcher(m.group(2));
			if (x.find()) {
				label = x.group(3);
			}
			if (label == null) {
				label = String.valueOf(id);
			}

			x = pXPosition.matcher(m.group(2));
			if (x.find()) {
				positionX = Float.parseFloat(x.group(2));
			}
			x = pYPosition.matcher(m.group(2));
			if (x.find()) {
				positionY = Float.parseFloat(x.group(2));
			}
			x = pSize.matcher(m.group(2));
			if (x.find()) {
				diameter = Float.parseFloat(x.group(2));
			}

			PositionShape position = new PositionShape(positionX, positionY);
			Stroke stroke = new Stroke(Constants.GRAPH_DB_NODE_STROKE);
			Fill fill = new Fill(Constants.GRAPH_DB_NODE_FILL);
			boolean isLabelling = false;
			boolean isVisible = true;
			diameter = Constants.GRAPH_DB_NODE_DIAMETER;
			int type = Constants.GRAPH_DB_NODE_TYPE;

			//only for case study kwan-liu ma
			// WordUtils.capitalize(label)
			
			if(label.equals("kwan-liu ma")){
				label = "Kwan-Liu Ma";
			}
			else if (!label.equals("*")) {
				String[] arrayLabel = label.split(" ");
				label = WordUtils.capitalize(WordUtils.initials(label).substring(0, 1).concat(". ")
						.concat(arrayLabel[arrayLabel.length - 1]));
			}

			Vertex node = new Vertex(id, label, type, position, stroke, fill, isLabelling, isVisible, diameter, weight);
			listNodes.add(node);
		}
		return listNodes;
	}

	/**
	 * Method to get all edges of a text line
	 * 
	 * @param edgeString
	 * @return List of edges
	 */
	public List<Edge> getEdges(String edgeString) {
		Matcher m;
		Matcher x;
		m = EDGE_PATTERN.matcher(edgeString);
		Pattern pId = Pattern.compile("(id.)(\\d*)");
		Pattern pLabel = Pattern.compile("(label.)(\")(.*)(\")");
		Pattern pNodeSource = Pattern.compile("(source.)(\\d*)");
		Pattern pNodeTarget = Pattern.compile("(target.)(\\d*)");
		Pattern pTypeEdge = Pattern.compile("(subgraph.)(\\d*)");

		int id;
		String label;
		int type;
		int nodeSource;
		int nodeTarget;
		List<Edge> listEdges = new ArrayList<Edge>();

		while (m.find()) {

			label = null;
			nodeSource = 0;
			nodeTarget = 0;
			type = 0;

			x = pId.matcher(m.group(2));
			if (x.find()) {
				id = Integer.parseInt(x.group(2));
			}
			x = pLabel.matcher(m.group(2));
			if (x.find()) {
				label = x.group(3);
			}
			x = pNodeSource.matcher(m.group(2));
			if (x.find()) {
				nodeSource = Integer.parseInt(x.group(2));
			}
			x = pNodeTarget.matcher(m.group(2));
			if (x.find()) {
				nodeTarget = Integer.parseInt(x.group(2));
			}
			x = pTypeEdge.matcher(m.group(2));
			if (x.find()) {
				type = Integer.parseInt(x.group(2));
			}

			if (label == null) {
				label = "Edge from " + nodeSource + " to " + nodeTarget;
			}

			id = 0;
			PositionShape position = null;
			Stroke stroke = new Stroke(false, ColorShape.getHSBGoogle_ColorCategory(type),
					Constants.GRAPH_DB_EDGE_STROKE_OPACITY, Constants.GRAPH_DB_EDGE_STROKE_WEIGHT);
			// LINES IN PROCESSING HAVE NOT FILL
			Fill fill = new Fill();
			double distance = 0.0d;
			boolean isLabelling = false;
			boolean isVisible = false;

			Edge edge = new Edge(id, label, type, position, stroke, fill, isLabelling, isVisible, nodeSource,
					nodeTarget, distance);
			listEdges.add(edge);
		}

		return listEdges;
	}

}