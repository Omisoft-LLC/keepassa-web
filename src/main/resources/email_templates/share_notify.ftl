<#-- @ftlvariable name="info" type="com.omisoft.keepassa.templates.SharePasswordTMP" -->
<#include "common/header.ftl">

<table width="100%" bgcolor="#e8eaed" cellpadding="0" cellspacing="0" border="0" id="backgroundTable">
  <tbody>
  <tr>
    <td>
      <table width="600" cellpadding="0" cellspacing="0" border="0" align="center" class="devicewidth">
        <tbody>
        <tr>
          <td width="100%">
            <table bgcolor="#ffffff" width="600" cellpadding="0" cellspacing="0" border="0" align="center" class="devicewidth">
              <tbody>
              <!-- Spacing -->
              <tr>
                <td width="100%" height="20"></td>
              </tr>
              <!-- Spacing -->
              <tr>
                <td>
                  <table width="560" align="center" cellpadding="0" cellspacing="0" border="0" class="devicewidthinner">
                    <tbody>
                    <!-- Title -->
                    <tr>
                      <td style="font-family: Helvetica, arial, sans-serif; font-size: 14px; font-weight:bold; color: #333333; text-align:left;line-height: 24px;">
                        You have been given access to password <b>${info.shareName}</b> by <b>${info.userEmail}</b>!
                      </td>
                    </tr>
                    <!-- End of Title -->
                    <!-- spacing -->
                    <tr>
                      <td height="5"></td>
                    </tr>
                    <!-- End of spacing -->
                    <!-- content -->

                    <!-- End of content -->
                    <!-- Spacing -->
                    <tr>
                      <td width="100%" height="5"></td>
                    </tr>
                    </tbody>
                  </table>
                </td>
              </tr>
              </tbody>
            </table>
          </td>
        </tr>
        </tbody>
      </table>
    </td>
  </tr>
  </tbody>
</table>
<#include "common/footer.ftl">



