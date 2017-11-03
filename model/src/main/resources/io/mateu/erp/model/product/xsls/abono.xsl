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
            <xsl:for-each select="factura">
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
                                                        CIF: <xsl:value-of select="destinatario/@cif"/>
                                                        - CC: <xsl:value-of select="destinatario/@cuenta"/>
                                                    </fo:block>
                                                    <fo:block font-size="12pt"><xsl:value-of select="destinatario/@nombrefiscal"/></fo:block>
                                                    <fo:block font-size="12pt"><xsl:value-of select="destinatario/@direccion"/></fo:block>
                                                    <fo:block font-size="12pt"><xsl:value-of select="destinatario/@cp"/> <xsl:value-of select="destinatario/@ciudad"/></fo:block>
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
                                                    <fo:block text-align="center" font-size="14pt">* * * * * A B O N O * * * * *</fo:block>
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
                                                        Nro ABONO: 12/<xsl:value-of select="@numero"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="1mm">
                                                    <fo:block text-align="right" font-size="12pt">
                                                        Palma, a <xsl:value-of select="@fecha"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>

                                    <fo:table>
                                        <fo:table-column column-width="10cm"/>
                                        <fo:table-column column-width="10cm"/>
                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell padding="2mm" number-columns-spanned="2">
                                                    <fo:block font-size="12pt">
                                                        Muy Sres. nuestros:
                                                        Les rogamos tomen nota que con esta fecha hemos contabilizado en su cuenta
                                                        (en concepto de "su abono") la cantidad abajo indicada y por los motivos que
                                                        a continuación les exponemos:
                                                    </fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-size="12pt">
                                                        MENOR VALOR DE SU FACTURA Nº
                                                    </fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-size="12pt">
                                                        <xsl:value-of select="@providerInvoiceNumber"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-size="12pt">
                                                        NRO BOOKING
                                                    </fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell padding="2mm">
                                                    <fo:block font-size="12pt">
                                                        <xsl:value-of select="grupo/linea/@localizador"/>
                                                    </fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                            <fo:table-row>
                                                <fo:table-cell padding="2mm" number-columns-spanned="2">
                                                    <fo:block font-size="12pt">
                                                        <xsl:apply-templates select="@subject"></xsl:apply-templates>
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
                                                                <xsl:for-each select="grupo">
                                                                    <fo:table-row>
                                                                        <fo:table-cell padding="1mm">
                                                                            <fo:block>
                                                                                Base Imponible
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                        <fo:table-cell padding="1mm">
                                                                            <fo:block>
                                                                                <xsl:value-of select="impuesto/@base"/>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                                                                    <fo:table-row>
                                                                        <fo:table-cell padding="1mm">
                                                                            <fo:block>
                                                                                <xsl:if test="impuesto/@porcentaje != '0.0'">
                                                                                    <xsl:value-of select="impuesto/@tipo"/> : <xsl:value-of select="impuesto/@porcentaje"/> %
                                                                                </xsl:if>
                                                                                <xsl:if test="impuesto/@porcentaje = '0.0'">
                                                                                    Impuestos incuidos (REAAVV)
                                                                                </xsl:if>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                        <fo:table-cell padding="1mm">
                                                                            <fo:block>
                                                                                <xsl:if test="impuesto/@total != '0.0'">

                                                                                    <xsl:value-of select="impuesto/@total"/>
                                                                                </xsl:if>
                                                                            </fo:block>
                                                                        </fo:table-cell>
                                                                    </fo:table-row>
                                                                </xsl:for-each>
                                                                <fo:table-row>
                                                                    <fo:table-cell padding="1mm">
                                                                        <fo:block>
                                                                            TOTAL:
                                                                        </fo:block>
                                                                    </fo:table-cell>
                                                                    <fo:table-cell padding="1mm">
                                                                        <fo:block>
                                                                            <xsl:value-of select="@total"/>
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
                                                    <fo:block><xsl:value-of select="facturadopor/@nombrefiscal"/></fo:block>
                                                    <fo:block>CIF: <xsl:value-of select="facturadopor/@cif"/></fo:block>
                                                    <fo:block><xsl:value-of select="facturadopor/@direccion"/></fo:block>
                                                    <fo:block><xsl:value-of select="facturadopor/@cp"/> <xsl:value-of select="facturadopor/@ciudad"/></fo:block>
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
