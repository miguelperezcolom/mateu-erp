<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:java="http://xml.apache.org/xslt/java" exclude-result-prefixes="java">
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master master-name="A4-portrait" page-height="29.7cm"
                                       page-width="21.0cm" margin="0.5cm">
                    <fo:region-body/>
                </fo:simple-page-master>
                <fo:simple-page-master master-name="A4-landscape" page-height="21.0cm"
                                       page-width="29.7cm">
                    <fo:region-body margin="0.5cm" margin-bottom="1cm"/>
                    <fo:region-after extent="1cm" padding-left="0.5cm" padding-right="0.5cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <xsl:for-each select="invoice">
                <fo:page-sequence master-reference="A4-portrait">
                    <fo:static-content flow-name="xsl-region-after" font-size="9pt">
                        <fo:block text-align="end" white-space-collapse="false" border-top="1px solid #000000" margin-top="1mm">Nro. Página:      <fo:page-number/></fo:block>
                    </fo:static-content>
                    <fo:flow flow-name="xsl-region-body" font-size="8pt">

                        <!--  CABECERA FACTURA -->
                        <fo:table>
                            <fo:table-column column-width="19cm"/>
                            <fo:table-body>
                                <fo:table-cell>
                                    <fo:table>
                                        <fo:table-column column-width="10.5cm"/>
                                        <fo:table-column column-width="8cm"/>
                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block font-size="12pt">
                                                        CIF: <xsl:value-of select="provider/cif"/>
                                                        - CC: <xsl:value-of select="provider/code"/>
                                                    </fo:block>
                                                    <fo:block font-size="12pt"><xsl:value-of select="provider/name"/></fo:block>
                                                    <fo:block font-size="12pt"><xsl:value-of select="provider/address"/></fo:block>
                                                    <fo:block font-size="12pt"><xsl:value-of select="provider/zip"/> <xsl:value-of select="provider/city"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block text-align="right">
                                                        <fo:external-graphic content-width="4.5cm" content-height="1.2cm" src="url('http://live.viajesurbis.com/vuweb/images/logo.GIF')"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>

                                    <fo:table>
                                        <fo:table-column column-width="19cm"/>
                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="10mm">
                                                    <fo:block text-align="center" font-size="14pt">FACTURA PROVEEDOR</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>
                                    <fo:table>
                                        <fo:table-column column-width="10.5cm"/>
                                        <fo:table-column column-width="8cm"/>
                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block text-align="left" font-size="12pt">
                                                        Su Nro factura: 12/<xsl:value-of select="@invoicenumber"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block text-align="right" font-size="12pt">
                                                        Palma, a <xsl:value-of select="@invoicedate"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>

                                    <fo:table>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="5cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-column column-width="2cm"/>
                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell><fo:block>Localizador</fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block>Descripción</fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block>Base</fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block>VAT</fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block>%</fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block>Total VAT</fo:block></fo:table-cell>
                                                <fo:table-cell><fo:block>Total</fo:block></fo:table-cell>
                                            </fo:table-row>
                                            <xsl:for-each select="lines/line">
                                                <fo:table-row>
                                                    <fo:table-cell><fo:block><xsl:value-of select="locator"/></fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block><xsl:value-of select="description"/></fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block><xsl:value-of select="base"/></fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block><xsl:value-of select="tax/@code"/></fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block><xsl:value-of select="tax/@percent"/></fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block><xsl:value-of select="tax"/></fo:block></fo:table-cell>
                                                    <fo:table-cell><fo:block><xsl:value-of select="total"/></fo:block></fo:table-cell>
                                                </fo:table-row>
                                            </xsl:for-each>
                                        </fo:table-body>
                                    </fo:table>


                                    <fo:table>
                                        <fo:table-column column-width="19cm"/>
                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block />
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>

                                    <fo:table font-size="12pt">
                                        <fo:table-column column-width="10cm"/>
                                        <fo:table-column column-width="10cm"/>
                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell />
                                                <fo:table-cell>
                                                    <fo:block>
                                                        <fo:table>
                                                            <fo:table-column column-width="5cm"/>
                                                            <fo:table-column column-width="5cm"/>
                                                            <fo:table-body>


                                                                <fo:table-row>
                                                                    <fo:table-cell padding="1mm">
                                                                        <fo:block>
                                                                            Base Imponible
                                                                        </fo:block>
                                                                    </fo:table-cell>
                                                                    <fo:table-cell padding="1mm">
                                                                        <fo:block>
                                                                            <xsl:value-of select="totalbase"/>
                                                                        </fo:block>
                                                                    </fo:table-cell>
                                                                </fo:table-row>
                                                                <fo:table-row>
                                                                    <fo:table-cell padding="1mm">
                                                                        <fo:block>
                                                                            <xsl:if test="taxpercent != '0.0'">
                                                                                IVA : <xsl:value-of select="taxpercent"/> %
                                                                            </xsl:if>
                                                                            <xsl:if test="taxpercent = '0.0'">
                                                                                Impuestos incuidos (REAAVV)
                                                                            </xsl:if>
                                                                        </fo:block>
                                                                    </fo:table-cell>
                                                                    <fo:table-cell padding="1mm">
                                                                        <fo:block>
                                                                            <xsl:if test="totaltax != '0.0'">

                                                                                <xsl:value-of select="totaltax"/>
                                                                            </xsl:if>
                                                                        </fo:block>
                                                                    </fo:table-cell>
                                                                </fo:table-row>

                                                                <fo:table-row>
                                                                    <fo:table-cell padding="1mm">
                                                                        <fo:block>
                                                                            TOTAL :
                                                                        </fo:block>
                                                                    </fo:table-cell>
                                                                    <fo:table-cell padding="1mm">
                                                                        <fo:block>
                                                                            <xsl:value-of select="totalinvoice"/> <fo:inline padding-left="3mm"><xsl:value-of select="currency"/></fo:inline>
                                                                        </fo:block>
                                                                    </fo:table-cell>
                                                                </fo:table-row>
                                                            </fo:table-body>
                                                        </fo:table>
                                                    </fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>

                                    <fo:table>
                                        <fo:table-column column-width="19cm"/>
                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block><xsl:value-of select="client/name"/></fo:block>
                                                    <fo:block>CIF: <xsl:value-of select="client/cif"/></fo:block>
                                                    <fo:block><xsl:value-of select="client/address"/></fo:block>
                                                    <fo:block><xsl:value-of select="client/zip"/> <xsl:value-of select="client/city"/></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>

                                </fo:table-cell>
                            </fo:table-body>
                        </fo:table>
                    </fo:flow>
                </fo:page-sequence>
            </xsl:for-each>
        </fo:root>
    </xsl:template>



    <xsl:template match="@subject">
        <xsl:call-template name="SplitText">
            <xsl:with-param name="inputString" select="." />
            <xsl:with-param name="delimiter" select="'&#x000A;'" />
        </xsl:call-template>
    </xsl:template>


    <xsl:template name="SplitText">
        <xsl:param name="inputString" />
        <xsl:param name="delimiter" />
        <xsl:choose>
            <xsl:when test="contains($inputString, $delimiter)">
                <xsl:value-of select="substring-before($inputString,$delimiter)" />
                <xsl:call-template name="SplitText">
                    <xsl:with-param name="inputString"
                                    select="substring-after($inputString,$delimiter)" />
                    <xsl:with-param name="delimiter" select="$delimiter" />
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$inputString"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
