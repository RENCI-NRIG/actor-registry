<html>
  <body>
    <center>

      <!-- Begin login form -->
      <form method="POST" action="j_security_check" name="loginForm">
        <table border="0" cellspacing="5">
          <tr>
            <td height="50">
	    <center>
              <h2>Please log in. </h2>
	      <h3><a href="http://ben.renci.org">BEN</a> LDAP Credentials required.</h3>
	    </center>
            </td>
          </tr>

          <!-- Username and password prompts fields layout -->
          <tr>
            <td>
              <table width="100%" border="0"
                     cellspacing="2" cellpadding="5">
                <tr>
                  <th align="right">
                    Username
                  </th>
                  <td align="left">
                    <input type="text" name="j_username" size="16"
                           maxlength="16"/>
                  </td>
                </tr>
                <p>
                <tr>
                  <th align="right">
                    Password
                  </th>
                  <td align="left">
                    <input type="password" name="j_password" size="16"
                           maxlength="16"/>
                  </td>
                </tr>

                <tr>
                  <td width="50%" valign="top"><div align="right" /></td>
                  <td width="55%" valign="top">&nbsp;</td>
                </tr>

                <!-- Login and reset buttons layout -->
                <tr>
                  <td width="50%" valign="top">
                    <div align="right">
                      <input type="submit" value='Login'>&nbsp;&nbsp;
                    </div>
                  </td>
                  <td width="55%" valign="top">
                    &nbsp;&nbsp;<input type="reset" value='Reset'>
                  </td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </form>

      <!-- End login form -->
    </center>

    <script language="JavaScript" type="text/javascript">
      <!--
      // Focus the username field when the page loads in the browser.
      document.forms["loginForm"].elements["j_username"].focus(  )
      // -->
    </script>

  </body>
</html>
