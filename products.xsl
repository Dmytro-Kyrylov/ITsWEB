<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
    <xsl:template match="/">
        <html>
            <head>
                <style type="text/css">
                    table.tfmt {
                    border: 1px ;
                    }
                    td.colfmt {
                    border: 1px ;
                    background-color: white;
                    color: black;
                    text-align:center;
                    }
                    th {
                    background-color: #2E9AFE;
                    color: white;
                    }

                </style>
            </head>
            <body>
                <table class="tfmt">
                    <tr>
                        <th>Product status:</th>
                        <th>Product reviews:</th>
                        <th>Product name:</th>
                        <th>Product brand:</th>
                        <th>Product price:</th>
                        <th>Product owners:</th>
                    </tr>
                    <xsl:for-each select="Products/makeUpProduct">
                        <tr>
                            <td class="colfmt">
                                <xsl:value-of select="status"/>
                                <xsl:if test="@count">
                                    (<xsl:value-of select="@count"/>)
                                </xsl:if>
                            </td>
                            <td class="colfmt">
                                <xsl:for-each select="reviews">
                                    <table>
                                        <tr>
                                            <th>Stars</th>
                                            <th>Comments</th>
                                        </tr>
                                        <tr>
                                            <td>
                                                <xsl:value-of select="stars"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="comments"/>
                                            </td>
                                        </tr>
                                    </table>
                                </xsl:for-each>
                            </td>
                            <td class="colfmt">
                                <xsl:value-of select="name"/>
                            </td>
                            <td class="colfmt">
                                <xsl:value-of select="brand"/>
                            </td>
                            <td class="colfmt">
                                <xsl:value-of select="price"/>
                            </td>
                            <xsl:if test="owners">
                                <td class="colfmt">
                                    <table>
                                        <tr>
                                            <th>First name</th>
                                            <th>Last name</th>
                                            <th>Phone</th>
                                        </tr>
                                        <xsl:for-each select="owners/owner">
                                            <tr>
                                                <td>
                                                    <xsl:value-of select="firstName"/>
                                                </td>
                                                <td>
                                                    <xsl:value-of select="lastName"/>
                                                </td>
                                                <td>
                                                    <xsl:value-of select="phone"/>
                                                </td>
                                            </tr>
                                        </xsl:for-each>
                                    </table>
                                </td>
                            </xsl:if>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>