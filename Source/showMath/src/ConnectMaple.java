/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.maplesoft.externalcall.MapleException;
import com.maplesoft.openmaple.Algebraic;
import com.maplesoft.openmaple.Engine;
import com.maplesoft.openmaple.EngineCallBacksDefault;
import com.maplesoft.openmaple.Expseq;
import com.maplesoft.openmaple.Procedure;

/**
 *
 * @author phatnguyen
 */
public class ConnectMaple {

	/**
	 * @param args
	 *            the command line arguments
	 * @throws MapleException
	 */
	public static void main(String args[]) throws MapleException

	{

		String mapleArgs[];

	    Engine engine;

	    mapleArgs = new String[1];

	    mapleArgs[0] = "java";

	    engine = new Engine( mapleArgs, new EngineCallBacksDefault(),

	        null, null );

	    engine.evaluate( "int(x,x);" );

	    engine.evaluate( "LinearAlgebra:-RandomMatrix( 3, 3 );" );

	    try
	    {

	        engine.evaluate( "syntax_error" );

	    }
	    catch ( MapleException me )

	    {

	        System.out.println( "Error: "+me.getMessage() );

	    }
	}

}
