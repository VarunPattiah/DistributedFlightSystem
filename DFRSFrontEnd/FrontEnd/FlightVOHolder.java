package FrontEnd;

/**
* FrontEnd/FlightVOHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/VarunPattiah/workspaceHelios/DFRSFrontEnd/src/FrontEnd.idl
* Monday, November 21, 2016 10:05:48 o'clock AM EST
*/

public final class FlightVOHolder implements org.omg.CORBA.portable.Streamable
{
  public FrontEnd.FlightVO value = null;

  public FlightVOHolder ()
  {
  }

  public FlightVOHolder (FrontEnd.FlightVO initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = FrontEnd.FlightVOHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    FrontEnd.FlightVOHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return FrontEnd.FlightVOHelper.type ();
  }

}