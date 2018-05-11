<%-- 
    Document   : config
    Created on : May 3, 2018, 10:16:37 AM
    Author     : Administrator
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import ="java.util.Properties"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Configure parameters</title>
    </head>
    <body>
        <form action="/testweb/ConfigServlet.do" method="POST">
            <table style="border-width: 0px">
                <%
                    Properties props = (Properties) request.getAttribute("properties");
                    for (String key : props.stringPropertyNames()) {
                %>
                <tr>
                    <%
                        String value = props.getProperty(key);
                        String strName = "";
                        switch (key) {
                            case "red_dis":
                                strName = "<td height='100' style='padding-right: 10px'>Khoảng cách bật đèn đỏ</td>";
                                break;
                            case "yello_dis":
                                strName = "<td height='100' style='padding-right: 10px'>Khoảng cách bật đèn vàng</td>";
                                break;
                            case "angten_lat":
                                strName = "<td height='100' style='padding-right: 10px'>Vĩ độ angten (latitude)</td>";
                                break;
                            case "angten_long":
                                strName = "<td height='100' style='padding-right: 10px'>Kinh độ angten (longitude)</td>";
                                break;
                        }
                        if (!strName.isEmpty()) {
                            out.println("<td>" + strName + "</td>");
                            out.println("<td><input type='text' name='" + key + "' value='" + value + "'></td>");
                        }
                    %>
                </tr>
                <%
                    }
                %>
                <tr><td colspan="2"><input type="submit" value="Submit"/></td></tr>
            </table>
        </form>
    </body>
</html>
