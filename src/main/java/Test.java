//import javax.naming.Context;
//import javax.naming.NamingEnumeration;
//import javax.naming.NamingException;
//import javax.naming.directory.*;
//import java.util.ArrayList;
//import java.util.Hashtable;
//import java.util.List;
//
///**
// * Created by nslavov on 2/3/17.
// */
//public class Test {
//
////  private static final String searchBase = "CN=Users,OU=Users,DC=OMISOFT,DC=local";
//  private static final String searchBase = "CN=Users,DC=OMISOFT,DC=local";
//  private static final String MASTER_PASSWORD = "asdqwe123!@#";
//  private static final String USER = "dido";
//  private static final String ldapUrl = "ldap://192.168.4.36";
//
//  private static final String DISPLAY_NAME = "displayName";
//  private static final String GIVEN_NAME = "givenName";
//  private static final String NAME = "name";
//  private static final String MEMBER_OF = "memberOf";
//  private static final String USER_PRINCIPAL_NAME = "userPrincipalName";
//  private static final String S_AM_ACCOUNT_NAME = "sAMAcocountName";
//
//
//  private static DirContext getContext() throws NamingException {
//
//    String initialContextFactory = "com.sun.jndi.ldap.LdapCtxFactory";
//    String securityAuthentication = "simple";
//    Hashtable<String, String> env = new Hashtable<>();
//    env.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
//    env.put(Context.SECURITY_AUTHENTICATION, securityAuthentication);
//    env.put(Context.PROVIDER_URL, ldapUrl);
//    env.put(Context.SECURITY_PRINCIPAL,USER);
//    env.put(Context.SECURITY_CREDENTIALS,MASTER_PASSWORD);
//    DirContext ctx = new InitialDirContext(env);
//    return ctx;
//  }
//
//
//  private static List<String> searchDisplay(DirContext context ,String name)
//      throws NamingException {
//    List<String> result = new ArrayList<>();
//
////    SearchControls searchControls = new SearchControls();
////    searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
////    searchControls.setReturningAttributes(new String[] {"distinguishedName"});
//    NamingEnumeration<SearchResult> searchResults = context.search("CN=Users,DC=OMISOFT,DC=local", String.format("(name=%s)", name), null);
//    if (!searchResults.hasMore()) {throw new NamingException();}
//
//    SearchResult searchResult = searchResults.next();
//    Attributes attributes = searchResult.getAttributes();
//    Attribute attribute = attributes.get("name");
//    String userObject = (String) attribute.get();
//    System.out.println(userObject);
//
//    return result;
//  }
//
//  public static void main(String[] args){
//    try {
//      DirContext context = getContext();
//      List<String> result = searchDisplay(context,"Nikolay Slavov");
//    } catch (NamingException e) {
//      e.printStackTrace();
//    }
//  }
//
//}
