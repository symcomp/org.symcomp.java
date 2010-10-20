package org.symcomp.frameworkDemo.server;

import org.symcomp.scscp.*;
import org.symcomp.openmath.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class OEISHandler extends ProcedureCallHandler {
	public String getServiceNameStr() { return "oeis"; }
	public String getDescription(OMSymbol oms) { return "oeis(lst): Consult the Online Encyclopedia of Integer Sequences about the integers in the list lst; oeis(n): Get the first few numbers in the Sequence labeled n in the library."; }

	private String escapeHTML(String sin) {
		/* this is really quite bad */
		StringBuffer sb = new StringBuffer();
		int n = sin.length();
		for (int i = 0; i < n; i++) {
			char c = sin.charAt(i);
			switch (c) {
				case '<': sb.append("&lt;"); break;
				case '>': sb.append("&gt;"); break;
				case '&': sb.append("&amp;"); break;
				case '"': sb.append("&quot;"); break;
				default: sb.append(c); break;
			}
		}
                return sb.toString();                                     
	}



	private String fetchWebpage(String address) 
	throws IOException, java.net.MalformedURLException {
		URL u = new URL(address);
		URLConnection uc = u.openConnection();
		InputStream in = uc.getInputStream();

		StringBuffer cont = new StringBuffer();
		final int bufsize = 2048;
		byte[] buffer = new byte[bufsize];

		int bytes_read = 0;
		while (true) {
			bytes_read = in.read(buffer, 0, bufsize);
			if (bytes_read >= 0) {
				cont.append(new String(buffer, 0, bytes_read, "ISO-8859-1"));
			} else {
				break;
			}
		}
		return cont.toString();		
	}

	private String queryFromList(OMApply apl)
	throws OpenMathException
	{
		OMSymbol hd = (OMSymbol) apl.getHead();
		if (!(hd.getCd().equals("list1") && hd.getName().equals("list"))
		&& !(hd.getCd().equals("linalg2") && hd.getName().equals("vector"))) {
			throw new OpenMathException( "First argument is not a list");
		}

		StringBuffer q = new StringBuffer();
		OpenMathBase[] lst = apl.getParams();
		for ( int i = 0; i < lst.length; ++i ) {
			OpenMathBase o = lst[i];
			if (!(o.getClass().toString().endsWith("OMInteger")))
				throw new OpenMathException("List elements not integers");
			q.append( ( (OMInteger) lst[i]).getStrValue() );
			if (i < lst.length - 1) q.append("%2C");
		}

		return q.toString();
	}
	private String queryFromString(OMString s) 
	throws OpenMathException {
		return s.getValue();
	}
	private String queryFromInteger(OMInteger i) 
	throws OpenMathException {
		String q = i.getStrValue();
		if (q.length() > 6) throw new OpenMathException("Invalid sequence number"); 
		while (q.length() < 6) q = "0" + q;
		return "A" + q;
	}
	
	private OMApply parseGetNames(String cont) {
		ArrayList<OpenMathBase> tmpParams = new ArrayList<OpenMathBase>();

		StringTokenizer st = new StringTokenizer(cont,"\n\r");
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			if (line.startsWith("no matches")) {
				tmpParams.add(new OMInteger(0));
			} else if (line.startsWith("Results")) {
				String k = line.replaceFirst("^Results ([0-9]+)\\-([0-9]+) of ([0-9]+) .*", "$3");
				tmpParams.add(new OMInteger(k));
			//} else if (line.startsWith("%I")) {
			//	tmpParams.add(new OMString(line.substring(3)));
			} else if (line.startsWith("%N")) {
				String k = line.replaceFirst("^%N (A[0-9]+) (.*)$", "$1: $2");
				tmpParams.add(new OMString(escapeHTML(k)));
			}
		}
		
		OpenMathBase[] t2 = new OpenMathBase[tmpParams.size()];
		tmpParams.toArray(t2);

		return new OMApply(new OMSymbol("list1", "list"), t2);
	}
	private OMApply parseGetSequence(String cont) 
	throws OpenMathException
	{
		ArrayList<OpenMathBase> tmpParams = new ArrayList<OpenMathBase>();

		StringTokenizer st = new StringTokenizer(cont, "\n\r");
		boolean insequence = false;
		while (st.hasMoreTokens()) {
			String line = st.nextToken();
			if (line.startsWith("no matches")) {
				break;
			} else if (line.startsWith("%S")
				|| ( insequence && !line.startsWith("%N")) ) {
				insequence = true;
				/* and parse this line */
				if (!line.matches("^%[A-Z] A[0-9]+ (.*)$")) throw new OpenMathException("Could not parse webpage");
				String line2 = line.replaceFirst("^%[A-Z] A[0-9]+ (.*)$", "$1");
				StringTokenizer st2 = new StringTokenizer(line2, ",");
				while (st2.hasMoreTokens()) {
					tmpParams.add( new OMInteger( st2.nextToken().trim() ));
				}

			} else if (line.startsWith("%N")) {
				break;
			}
		}

		OpenMathBase[] t2 = new OpenMathBase[tmpParams.size()];
		tmpParams.toArray(t2);

		return new OMApply(new OMSymbol("list1", "list"), t2);
	}

	public OpenMathBase handlePayload(OpenMathBase omb) 
	throws OpenMathException
	{
		String q;

		/* OMApply omb contains the call to OEIS as 1st argument, so we will bypass that.
		   Then we get the actual argument to the procedure call, p, which could be
		     an OMApply, an OMInteger, or an OMString. Depending on what it is, we construct
			 a different query to the OEIS. */
		OpenMathBase p = ((OMApply) omb).getParams()[0];
		String tp = p.getClass().toString();
		System.out.println("tp = " + tp);
		try {

			if (tp.endsWith("OMApply")) {
				q = queryFromList((OMApply) p);
			} else if (tp.endsWith("OMInteger")) {
				q = queryFromInteger((OMInteger) p);
			} else if (tp.endsWith("OMString")) {
				q = queryFromString((OMString) p);
			} else {
				throw new OpenMathException("Invalid argument");
			}
		} catch (OpenMathException e) {
			throw new OpenMathException(e.getMessage());
		}

		/* do web site request */
		String address = "http://www.research.att.com/~njas/sequences/?q=" + q + "&fmt=3";
		String cont;
		try {
			cont = fetchWebpage(address);
		} catch (Exception e) {
			e.printStackTrace();
			throw new OpenMathException("Could not access online database");
		}

		/* parse web site request */
		OMApply res;
		if (tp.endsWith("OMInteger") || tp.endsWith("OMString")) {
			res = parseGetSequence(cont);
		} else if (tp.endsWith("OMApply")) {
			res = parseGetNames(cont);
		} else {
			throw new OpenMathException("This should not happen");
		}

		/* return result */
		return res;
	}

}

