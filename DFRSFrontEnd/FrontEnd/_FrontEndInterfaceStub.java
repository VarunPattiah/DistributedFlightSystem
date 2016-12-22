package FrontEnd;


/**
* FrontEnd/_FrontEndInterfaceStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from C:/Users/VarunPattiah/workspaceHelios/DFRSFrontEnd/src/FrontEnd.idl
* Monday, November 21, 2016 10:05:48 o'clock AM EST
*/

public class _FrontEndInterfaceStub extends org.omg.CORBA.portable.ObjectImpl implements FrontEnd.FrontEndInterface
{

  public String executeOperation (String method, String managerId, String firstName, String lastName, String phoneNumber, String destination, String address, String dateofBooking, String classType, String recordType, String recordId, String fieldName, String passengerID, String currentCity, String otherCity, String newValue, String remoteServerName)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("executeOperation", true);
                $out.write_string (method);
                $out.write_string (managerId);
                $out.write_string (firstName);
                $out.write_string (lastName);
                $out.write_string (phoneNumber);
                $out.write_string (destination);
                $out.write_string (address);
                $out.write_string (dateofBooking);
                $out.write_string (classType);
                $out.write_string (recordType);
                $out.write_string (recordId);
                $out.write_string (fieldName);
                $out.write_string (passengerID);
                $out.write_string (currentCity);
                $out.write_string (otherCity);
                $out.write_string (newValue);
                $out.write_string (remoteServerName);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return executeOperation (method, managerId, firstName, lastName, phoneNumber, destination, address, dateofBooking, classType, recordType, recordId, fieldName, passengerID, currentCity, otherCity, newValue, remoteServerName        );
            } finally {
                _releaseReply ($in);
            }
  } // executeOperation

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:FrontEnd/FrontEndInterface:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _FrontEndInterfaceStub