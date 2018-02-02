<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output indent="yes"/>
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>

                <!-- A4 size -->
                <fo:simple-page-master master-name="dinA4" page-height="29.7cm" page-width="21cm" margin="2mm 20mm 26mm 20mm">
                    <fo:region-body margin-top="36mm"/>
                    <fo:region-before display-align="after"/>
                    <fo:region-after display-align="after"/>
                </fo:simple-page-master>

                <fo:simple-page-master master-name="last" page-height="29.7cm" page-width="21cm" margin="15mm 20mm 32mm 20mm">
                    <fo:region-body margin-top="36mm" column-count="3"/>
                    <fo:region-before display-align="after"/>
                    <fo:region-after display-align="after"/>
                </fo:simple-page-master>

            </fo:layout-master-set>

            <xsl:for-each select="//contract">

                <fo:page-sequence master-reference="dinA4">

                    <!-- Header -->
                    <fo:static-content flow-name="xsl-region-before">
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column column-width="54mm"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.3px"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.3px"></fo:table-column>

                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="left"  font-size="8pt">
                                        <fo:block><fo:external-graphic src="{@logo}" content-height="scale-to-fit" height="13.3mm" content-width="scale-to-fit" width="31.2mm"/></fo:block>
                                        <fo:block><xsl:value-of select="@bussinessName"></xsl:value-of></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="9pt" font-weight="bold" padding="1mm">
                                        <fo:block><xsl:value-of select="@title"></xsl:value-of></fo:block>
                                        <fo:block><fo:inline color="white">x</fo:inline></fo:block>
                                        <fo:block><xsl:value-of select="@type"/> - Nº<xsl:value-of select="@id"/></fo:block>
                                        <fo:block><fo:inline color="white">x</fo:inline></fo:block>
                                        <fo:block><xsl:value-of select="@currencyCode"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="8pt" padding="1mm">
                                        <fo:block font-weight="bold"><xsl:value-of select="hotel/@name"/> <xsl:value-of select="hotel/@category"/> PMI</fo:block>
                                        <fo:block><xsl:value-of select="supplier/@bussinessName"/></fo:block>
                                        <fo:block><xsl:value-of select="supplier/@vatIdentificationNumber"/></fo:block>
                                        <fo:block><xsl:value-of select="hotel/@address"/></fo:block>
                                        <fo:block>t:<xsl:value-of select="hotel/@tel"/></fo:block>
                                        <fo:block>f:<xsl:value-of select="hotel/@fax"/></fo:block>
                                        <fo:block><xsl:value-of select="hotel/@email"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                        </fo:table>
                    </fo:static-content>

                    <!-- Footer -->
                    <fo:static-content flow-name="xsl-region-after">

                        <fo:block color="white">x</fo:block>

                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column column-width="54mm" border-right-style="solid" border-right-width="0.3px"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.3px"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.3px"></fo:table-column>

                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="right" font-size="8pt" padding="1mm" display-align="after">
                                            <fo:block>And as proof of consent and acceptance of all the clauses , including those contained in page <fo:page-number-citation-last ref-id="end"/>, the two sides signed in <xsl:value-of select="@signedAt"/>, <xsl:value-of select="@signatureDate"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="8pt" font-weight="bold" padding="1mm" display-align="after">
                                        <fo:block>Name and surname</fo:block>
                                        <fo:block>By <xsl:value-of select="@partnerSignatory"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="8pt" font-weight="bold" padding="1mm" display-align="after">
                                        <fo:block>Name and surname</fo:block>
                                        <fo:block>By <xsl:value-of select="@ownSignatory"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                        </fo:table>

                        <fo:block text-align="right" font-size="8pt" font-family="Liberation Sans Narrow">Page <fo:page-number></fo:page-number> of <fo:page-number-citation-last ref-id="end"/></fo:block>

                    </fo:static-content>

                    <!-- Content -->
                    <fo:flow flow-name="xsl-region-body" >

                        <!-- Acuerdo comercial, 1 y 2 -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column></fo:table-column>

                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt">COMMERCIAL AGREEMENT <!-- <fo:inline  font-weight="100" font-size="8pt" font-style="italic">ACUERDO COMERCIAL</fo:inline> --></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell font-size="8pt" margin-left="54mm" padding="1mm">
                                        <fo:block>Gathered the parties <fo:inline font-weight="bold"><xsl:value-of select="contractor/@name"/></fo:inline> --<xsl:value-of select="contractor/@bussinessName"/>-- (THE CUSTOMER) and <fo:inline font-weight="bold"><xsl:value-of select="hired/@nombre"/></fo:inline> --<xsl:value-of select="hired/@descripcion"/>-- (THE SUPPLIER) agree as follows:</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt"></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt">1. Agreement validity <!-- <fo:inline  font-weight="100" font-size="8pt" font-style="italic">Validez acuerdo</fo:inline> --></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell font-size="8pt" margin-left="54mm" padding="1mm">
                                        <fo:block>For stays from <xsl:value-of select="@validFrom"/> to <xsl:value-of select="@validTo"/>.</fo:block>
                                        <xsl:if test="@bookingWindowFrom or @bookingWindowTo"><fo:block>For bookings done <xsl:if test="@bookingWindowFrom"> from <xsl:value-of select="@bookingWindowFrom"/></xsl:if> <xsl:if test="@bookingWindowTo"> to <xsl:value-of select="@bookingWindowTo"/></xsl:if>.</fo:block></xsl:if>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell font-size="8pt" margin-left="54mm" padding="1mm"><fo:block>
                                        <xsl:choose>
                                            <xsl:when test="targets/*">
                                                <xsl:choose>
                                                    <xsl:when test="targets/*[@included]">
                                                        For <xsl:for-each select="targets/*[@included]"><xsl:if test="position() > 1">, </xsl:if><xsl:value-of select="."/></xsl:for-each>.
                                                    </xsl:when></xsl:choose>
                                                <xsl:choose>
                                                    <xsl:when test="targets/*[@excluded]">
                                                        Not for <xsl:for-each select="targets/*[@excluded]"><xsl:if test="position() > 1">, </xsl:if><xsl:value-of select="."/></xsl:for-each>.
                                                    </xsl:when></xsl:choose>
                                            </xsl:when>
                                            <xsl:otherwise>Generally available.</xsl:otherwise>
                                        </xsl:choose>
                                    </fo:block></fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt"></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                            <xsl:if test="@specialTerms">
                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                            <fo:block font-weight="700" font-size="12pt">1.1. Special terms <!-- <fo:inline  font-weight="100" font-size="8pt" font-style="italic">ACUERDO COMERCIAL</fo:inline> --></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row>
                                        <fo:table-cell font-size="8pt" margin-left="54mm" padding="1mm">
                                            <fo:block><xsl:value-of select="@specialTerms"/></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block font-weight="700" font-size="12pt"></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </xsl:if>

                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt">2. Contracted rooms</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell padding="1mm" font-size="8pt" margin-left="54mm">
                                        <fo:block>Types, capacities , periods , regimes and prices (<xsl:value-of select="terms/@ratesType"/> stated in <xsl:value-of select="@currencyCode"/>, including taxes) , under the conditions listed below.</fo:block>
                                        <fo:block>Supplements / discounts , indicated by + or - , they are Pax / night.</fo:block>
                                        <fo:block>
                                            <xsl:if test="hotel/@childStartAge">Baby until <xsl:value-of select="hotel/@childStartAge"/> (not included).</xsl:if>
                                            <xsl:if test="hotel/@juniorStartAge">Child until <xsl:value-of select="hotel/@childStartAge"/> (not included).</xsl:if>
                                            <xsl:if test="hotel/@adultStartAge">Junior until <xsl:value-of select="hotel/@adultStartAge"/> (not included).</xsl:if>
                                            Children will be ordered <xsl:choose><xsl:when test="hotel/@youngestFirst">from youngest to oldest</xsl:when><xsl:otherwise>from oldest to youngest</xsl:otherwise></xsl:choose> for discounts application.
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>


                        </fo:table>



                        <xsl:for-each select="terms/rooms/room">

                            <fo:block page-break-inside="avoid">
                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                    <fo:table-column column-width="55mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>


                                    <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell padding="1mm">
                                                <fo:block color="white">x</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row>
                                            <fo:table-cell number-columns-spanned="1" text-align="right" font-size="12pt" padding="1mm" font-weight="bold">
                                                <fo:block><xsl:value-of select="@id"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell number-columns-spanned="7" text-align="left" font-size="8pt" padding="1mm">
                                                <fo:block><fo:inline font-weight="bold"><xsl:value-of select="@name"/></fo:inline> in <fo:inline font-weight="bold"><xsl:value-of select="@board"/></fo:inline>. <xsl:if test="@nonrefundable"><fo:inline font-weight="bold">NON REFUNDABLE</fo:inline></xsl:if></fo:block>
                                                <fo:block><xsl:value-of select="@capacity"/></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>


                                        <xsl:for-each select="row">

                                            <fo:table-row>
                                                <fo:table-cell>
                                                    <fo:block></fo:block>
                                                </fo:table-cell>
                                                <xsl:for-each select="dates/ranges">
                                                    <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold">
                                                        <xsl:for-each select="range"><fo:block><xsl:value-of select="@start"/></fo:block><fo:block><xsl:value-of select="@end"/></fo:block></xsl:for-each>
                                                    </fo:table-cell>
                                                </xsl:for-each>
                                            </fo:table-row>
                                            <xsl:for-each select="line[@tipo = 'base']">
                                                <fo:table-row>
                                                    <xsl:variable name="pos" select="position()"></xsl:variable>
                                                    <fo:table-cell
                                                            text-align="right"
                                                            font-size="8pt"
                                                            padding="1mm"
                                                            font-weight="bold"
                                                            border-right-style="solid"
                                                            border-right-width="0.3px">
                                                        <xsl:if test="$pos = 1"><xsl:attribute name="border-before-style">solid</xsl:attribute><xsl:attribute name="border-before-width">0.3px</xsl:attribute></xsl:if>
                                                        <fo:block><xsl:value-of select="@description"/></fo:block>
                                                    </fo:table-cell>
                                                    <xsl:for-each select="price">
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="8pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                            <xsl:if test="$pos = 1"><xsl:attribute name="border-before-style">solid</xsl:attribute><xsl:attribute name="border-before-width">0.3px</xsl:attribute></xsl:if>
                                                            <fo:block><xsl:value-of select="."/></fo:block>
                                                        </fo:table-cell>
                                                    </xsl:for-each>
                                                </fo:table-row>
                                            </xsl:for-each>
                                            <xsl:for-each select="line[@tipo = 'suplemento' and price]">
                                                <xsl:variable name="pos" select="position()"></xsl:variable>
                                                <fo:table-row>
                                                    <fo:table-cell
                                                            text-align="right"
                                                            font-size="8pt"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="0.3px">
                                                        <fo:block><xsl:value-of select="@description"/></fo:block>
                                                    </fo:table-cell>
                                                    <xsl:for-each select="price">
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="8pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                            <xsl:if test="$pos = 1"><xsl:attribute name="border-before-style">dashed</xsl:attribute><xsl:attribute name="border-before-width">thin</xsl:attribute></xsl:if>
                                                            <fo:block><xsl:value-of select="."/></fo:block>
                                                        </fo:table-cell>
                                                    </xsl:for-each>
                                                </fo:table-row>
                                            </xsl:for-each>

                                        </xsl:for-each>


                                    </fo:table-body>

                                </fo:table>
                            </fo:block>
                        </xsl:for-each>


                        <!-- 3. Release -->
                        <xsl:if test="terms/releases/rule">
                            <fo:block page-break-inside="avoid">
                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                                    <fo:table-column></fo:table-column>

                                    <fo:table-body>

                                        <fo:table-row>
                                            <fo:table-cell padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt"></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <fo:table-row>
                                            <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt">3. Release <!--  <fo:inline  font-weight="100" font-size="8pt" font-style="italic">Release</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <fo:table-row>
                                            <fo:table-cell font-size="8pt" margin-left="54mm" padding="1mm">
                                                <fo:block>
                                                    If not stated below release 0 will be applied.
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                    </fo:table-body>
                                </fo:table>

                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                    <fo:table-column column-width="14mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="50mm"></fo:table-column>

                                    <fo:table-header>
                                        <fo:table-row>
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold">
                                                <fo:block>from</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold">
                                                <fo:block>to</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold">
                                                <fo:block>release</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold">
                                                <fo:block>rooms</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-header>

                                    <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px"
                                                    border-before-style="solid"
                                                    border-before-width="0.3px">
                                                <xsl:for-each select="terms/releases/rule">
                                                    <fo:block><xsl:value-of select="@start"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px"
                                                    border-before-style="solid"
                                                    border-before-width="0.3px">
                                                <xsl:for-each select="terms/releases/rule">
                                                    <fo:block><xsl:value-of select="@end"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px"
                                                    border-before-style="solid"
                                                    border-before-width="0.3px">
                                                <xsl:for-each select="terms/releases/rule">
                                                    <fo:block><xsl:value-of select="@release"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px"
                                                    border-before-style="solid"
                                                    border-before-width="0.3px">
                                                <xsl:for-each select="terms/releases/rule">
                                                    <fo:block><xsl:for-each select="room"><xsl:value-of select="@id"/> </xsl:for-each></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            </fo:block>
                        </xsl:if>
                        <!-- 4. Cupos -->

                        <fo:block page-break-inside="avoid">

                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                                <fo:table-column></fo:table-column>

                                <fo:table-body>

                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block font-weight="700" font-size="12pt"></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                            <fo:block font-weight="700" font-size="12pt">4. Allotment <!-- <fo:inline  font-weight="100" font-size="8pt" font-style="italic">Cupo</fo:inline> --></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:if test="@aceptaonrequest">
                                        <fo:table-row>
                                            <fo:table-cell font-size="8pt" margin-left="54mm" padding="1mm">
                                                <fo:block>
                                                    When no allotment is available bookings are allowed ON REQUEST.
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:if>


                                </fo:table-body>
                            </fo:table>

                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                <fo:table-column column-width="14mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>
                                <fo:table-column column-width="20mm"></fo:table-column>

                                <fo:table-header>
                                    <fo:table-row>
                                        <fo:table-cell>
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell>
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                            <fo:block>room</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                            <fo:block>from</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                            <fo:block>to</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                            <fo:block>allotment</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-header>

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell>
                                            <fo:block></fo:block>
                                        </fo:table-cell>

                                        <fo:table-cell
                                                number-columns-spanned="2"
                                                text-align="right"
                                                font-size="8pt"
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="0.3px">
                                            <xsl:for-each select="terms/allotment/allotment">
                                                <fo:block><xsl:value-of select="@room"/></fo:block>
                                            </xsl:for-each>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                text-align="right"
                                                font-size="8pt"
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="0.3px">
                                            <xsl:for-each select="terms/allotment/allotment">
                                                <fo:block><xsl:value-of select="@start"/></fo:block>
                                            </xsl:for-each>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                text-align="right"
                                                font-size="8pt"
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="0.3px">
                                            <xsl:for-each select="terms/allotment/allotment">
                                                <fo:block><xsl:value-of select="@start"/></fo:block>
                                            </xsl:for-each>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                text-align="right"
                                                font-size="8pt"
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="0.3px">
                                            <xsl:for-each select="terms/allotment/allotment">
                                                <fo:block><xsl:value-of select="@quantity"/></fo:block>
                                            </xsl:for-each>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>
                        </fo:block>
                        <xsl:if test="cupogarantizado">

                            <!-- 4.1. Cupo garantizado -->
                            <fo:block page-break-inside="avoid">
                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                                    <fo:table-column></fo:table-column>

                                    <fo:table-body>

                                        <fo:table-row>
                                            <fo:table-cell padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt"></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <fo:table-row>
                                            <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt">4.1. Guaranteed allotment <!--  <fo:inline  font-weight="100" font-size="8pt" font-style="italic">Cupo garantizado</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <fo:table-row>
                                            <fo:table-cell font-size="8pt" margin-left="54mm" padding="1mm">
                                                <fo:block>
                                                    <xsl:choose>
                                                        <xsl:when test="cupogarantizado/@seguridad">This is SECURITY allotment (activated when stop sales applied)</xsl:when>
                                                        <xsl:otherwise>This is MINIMUM allotment (activated when stop sales applied, but affected by existent bookings)</xsl:otherwise>
                                                    </xsl:choose>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                    </fo:table-body>
                                </fo:table>

                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                    <fo:table-column column-width="14mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>

                                    <fo:table-header>
                                        <fo:table-row>
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>room</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>from</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>to</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>allotment</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-header>

                                    <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>

                                            <fo:table-cell
                                                    number-columns-spanned="2"
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="cupogarantizado/cupo">
                                                    <fo:block><xsl:for-each select="habitacion"><xsl:if test="position() > 1"> | </xsl:if><xsl:value-of select="@id"/></xsl:for-each></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="cupogarantizado/cupo">
                                                    <fo:block><xsl:value-of select="@del"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="cupogarantizado/cupo">
                                                    <fo:block><xsl:value-of select="@al"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="cupogarantizado/cupo">
                                                    <fo:block><xsl:value-of select="@numero"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            </fo:block>

                        </xsl:if>

                        <!-- 6. Suplementos --><xsl:if test="terms/supplements/supplement">
                        <fo:block page-break-inside="avoid">
                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">
                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block font-weight="700" font-size="12pt"></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row>
                                        <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                            <fo:block font-weight="700" font-size="12pt">5. Supplements and discounts<!-- <fo:inline  font-weight="100" font-size="8pt" font-style="italic">Suplementos</fo:inline>--></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>

                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                <fo:table-column column-width="174mm"></fo:table-column>

                                <fo:table-header>
                                    <fo:table-row display-align="after">
                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                            <fo:block>Description</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-header>

                                <fo:table-body>

                                    <xsl:for-each select="terms/supplements/supplement">


                                        <fo:table-row>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <fo:block><xsl:value-of select="@descriptionforpdf"/></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>
                                </fo:table-body>
                            </fo:table>
                        </fo:block>
                    </xsl:if>
                        <!-- 6. Estáncias mínimas -->
                        <xsl:if test="terms/minimumStays/rule">
                            <fo:block page-break-inside="avoid">
                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">
                                    <fo:table-body>

                                        <fo:table-row>
                                            <fo:table-cell padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt"></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <fo:table-row>
                                            <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt">6. Minimum stay <!-- <fo:inline  font-weight="100" font-size="8pt" font-style="italic">Estáncias mínimas</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row>
                                            <fo:table-cell font-size="8pt" margin-left="54mm" padding="1mm">
                                                <fo:block>The conditions apply when booking for a number of nights fewer than indicated</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>

                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="114mm"></fo:table-column>

                                    <fo:table-header>
                                        <fo:table-row display-align="after">
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>from</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>to</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>nights</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>action</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-header>

                                    <fo:table-body>

                                        <fo:table-row>

                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/minimumStays/rule">
                                                    <fo:block><xsl:value-of select="@start"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/minimumStays/rule">
                                                    <fo:block><xsl:value-of select="@end"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/minimumStays/rule">
                                                    <fo:block><xsl:value-of select="@nights"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/minimumStays/rule">
                                                    <fo:block><xsl:value-of select="@description"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            </fo:block>
                        </xsl:if>
                        <!-- 6. Días entrada -->
                        <xsl:if test="terms/weekDays/rule">
                            <fo:block page-break-inside="avoid">
                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">
                                    <fo:table-body>

                                        <fo:table-row>
                                            <fo:table-cell padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt"></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <fo:table-row>
                                            <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt">7. Mandatory checkin, stay and checkout days <!-- <fo:inline  font-weight="100" font-size="8pt" font-style="italic">Dias de entrada y salida obligatorios</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>

                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                    <fo:table-column column-width="14mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="40mm"></fo:table-column>

                                    <fo:table-header>
                                        <fo:table-row display-align="after">
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>from</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>to</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>week days</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>month days</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>checkin/out</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>result when not accomplished</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-header>

                                    <fo:table-body>

                                        <fo:table-row>
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>

                                            <fo:table-cell
                                                    number-columns-spanned="2"
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/weekDays/rule">
                                                    <fo:block><xsl:value-of select="@start"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/weekDays/rule">
                                                    <fo:block><xsl:value-of select="@end"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/weekDays/rule">
                                                    <fo:block><xsl:value-of select="@weekDays"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/weekDays/rule">
                                                    <fo:block><xsl:value-of select="@diasmes"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/weekDays/rule">
                                                    <fo:block><xsl:choose><xsl:when test="@salida">checkout</xsl:when><xsl:otherwise>checkin</xsl:otherwise></xsl:choose></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="8pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/weekDays/rule">
                                                    <fo:block><xsl:choose><xsl:when test="@dejaronrequest">leave on request</xsl:when><xsl:otherwise>not allow</xsl:otherwise></xsl:choose></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            </fo:block>
                        </xsl:if>

                        <!-- 7. Ofertas varias -->
                        <xsl:if test="ofertas">
                            <fo:block page-break-inside="avoid">
                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">
                                    <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt"></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row>
                                            <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt">8. Offers <!-- <fo:inline  font-weight="100" font-size="8pt" font-style="italic">Ofertas</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>

                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
                                    <fo:table-column column-width="15mm"></fo:table-column>
                                    <fo:table-column column-width="15mm"></fo:table-column>
                                    <fo:table-column column-width="30mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>

                                    <fo:table-header>
                                        <fo:table-row display-align="after">
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>Formalization</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>stay</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>checkin</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>release</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>min.nights</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>description</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>discount</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>boards/rooms</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>cancellation</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-header>

                                    <fo:table-body>
                                        <xsl:for-each select="ofertas/oferta">
                                            <fo:table-row>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@formdel"/></fo:block><fo:block><xsl:value-of select="@formal"/></fo:block>
                                                </fo:table-cell>

                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@estdel"/></fo:block><fo:block><xsl:value-of select="@estal"/></fo:block>
                                                </fo:table-cell>

                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@entdel"/></fo:block><fo:block><xsl:value-of select="@ental"/></fo:block>
                                                </fo:table-cell>

                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@release"/></fo:block>
                                                </fo:table-cell>

                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@estanciaminima"/></fo:block>
                                                </fo:table-cell>

                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@descripcion"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@descuento"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@regimenes"/> /</fo:block><fo:block><xsl:value-of select="@habitaciones"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@cancelacion"/></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </xsl:for-each>
                                    </fo:table-body>
                                </fo:table>
                            </fo:block>
                        </xsl:if>

                        <!-- 8. Condiciones de cancelación -->
                        <xsl:if test="terms/cancellation/rule">
                            <fo:block page-break-inside="avoid">
                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">
                                    <fo:table-body>
                                        <fo:table-row>
                                            <fo:table-cell padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt"></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row>
                                            <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                                <fo:block font-weight="700" font-size="12pt">9. Cancellation terms <!-- <fo:inline  font-weight="100" font-size="8pt" font-style="italic">Condiciones de cancelación</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>

                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                    <fo:table-column column-width="14mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="40mm"></fo:table-column>

                                    <fo:table-header>
                                        <fo:table-row display-align="after">
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell>
                                                <fo:block></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>from</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>to</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>release</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>action</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell number-columns-spanned="2" text-align="right" font-size="8pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>supplement</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-header>

                                    <fo:table-body>
                                        <xsl:for-each select="terms/cancellation/rule">
                                            <fo:table-row>
                                                <fo:table-cell>
                                                    <fo:block></fo:block>
                                                </fo:table-cell>

                                                <fo:table-cell>
                                                    <fo:block></fo:block>
                                                </fo:table-cell>

                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@start"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@end"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@release"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@permitir"/></fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        number-columns-spanned="2"
                                                        text-align="right"
                                                        font-size="8pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <fo:block><xsl:value-of select="@suplemento"/></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </xsl:for-each>
                                    </fo:table-body>
                                </fo:table>

                            </fo:block>

                        </xsl:if>


                    </fo:flow>
                </fo:page-sequence>

                <fo:page-sequence master-reference="last">

                    <!-- Header -->
                    <fo:static-content flow-name="xsl-region-before">
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column column-width="54mm"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.3px"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.3px"></fo:table-column>

                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="left"  font-size="8pt">
                                        <fo:block><fo:external-graphic src="{nosotros/@logo}" content-height="scale-to-fit" height="13.3mm" content-width="scale-to-fit" width="31.2mm"/></fo:block>
                                        <fo:block><xsl:value-of select="nosotros/@descripcion"></xsl:value-of></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="9pt" font-weight="bold" padding="1mm">
                                        <fo:block><xsl:value-of select="@titulo"></xsl:value-of></fo:block>
                                        <fo:block><fo:inline color="white">x</fo:inline></fo:block>
                                        <fo:block><xsl:value-of select="@tipo"/> - Nº<xsl:value-of select="@id"/></fo:block>
                                        <fo:block><fo:inline color="white">x</fo:inline></fo:block>
                                        <fo:block><xsl:value-of select="@moneda"></xsl:value-of></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="8pt" padding="1mm">
                                        <fo:block font-weight="bold"><xsl:value-of select="objeto/@nombre"/> <xsl:value-of select="objeto/@categoria"/> PMI</fo:block>
                                        <fo:block><xsl:value-of select="objeto/@nombrefiscal"/></fo:block>
                                        <fo:block><xsl:value-of select="objeto/@cif"/></fo:block>
                                        <fo:block><xsl:value-of select="objeto/@direccion"/></fo:block>
                                        <fo:block>t:<xsl:value-of select="objeto/@telefono"/></fo:block>
                                        <fo:block>f:<xsl:value-of select="objeto/@fax"/></fo:block>
                                        <fo:block><xsl:value-of select="objeto/@email"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                        </fo:table>
                    </fo:static-content>

                    <!-- Footer -->
                    <fo:static-content flow-name="xsl-region-after">

                        <fo:block color="white">x</fo:block>

                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column column-width="54mm" border-right-style="solid" border-right-width="0.3px"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.3px"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.3px"></fo:table-column>

                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="right" font-size="8pt" padding="1mm" display-align="after">
                                        <fo:block>And as proof of consent and acceptance of all the clauses , including those contained in page <fo:page-number-citation-last ref-id="end"/>, the two sides signed in <xsl:value-of select="@firmadoen"/>, <xsl:value-of select="@fecha"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="8pt" font-weight="bold" padding="1mm" display-align="after">
                                        <fo:block>Name and surname</fo:block>
                                        <fo:block>By <xsl:value-of select="entre/@nombre"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="8pt" font-weight="bold" padding="1mm" display-align="after">
                                        <fo:block>Name and surname</fo:block>
                                        <fo:block>By <xsl:value-of select="y/@nombre"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                        </fo:table>

                        <fo:block text-align="right" font-size="8pt" font-family="Liberation Sans Narrow">Page <fo:page-number></fo:page-number> of <fo:page-number-citation-last ref-id="end"/></fo:block>

                    </fo:static-content>

                    <!-- Content -->

                    <fo:flow flow-name="xsl-region-body">

                        <!-- Cláusulas del contrato -->
                        <fo:block font-size="7.7pt" font-family="Liberation Sans Narrow" font-weight="bold" space-before="5mm" space-after="1mm">
                            Contract clauses
                        </fo:block>

                        <xsl:if test="clausulas/clausula or @porcentajemarketing">

                            <fo:list-block font-size="7.7pt" font-family="Liberation Sans Narrow" provisional-distance-between-starts="0.3cm" provisional-label-separation="0.15cm">

                                <xsl:for-each select="clausulas/clausula">

                                    <fo:list-item  padding="1mm">
                                        <fo:list-item-label end-indent="label-end()">
                                            <fo:block>
                                                <xsl:value-of select="position()"></xsl:value-of>
                                            </fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block>
                                                <xsl:value-of select="."/>
                                            </fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                </xsl:for-each>

                                <xsl:if test="@porcentajemarketing">
                                    <fo:list-item padding="1mm">
                                        <fo:list-item-label end-indent="label-end()">
                                            <fo:block>
                                                <xsl:value-of select="count(clausulas/clausula) + 1"></xsl:value-of>
                                            </fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block>
                                                Marketing contribution <xsl:value-of select="@porcentajemarketing"/> %
                                            </fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                </xsl:if>

                            </fo:list-block>

                        </xsl:if>

                        <!-- Leyendas del contrato -->
                        <fo:block font-size="7.7pt" font-family="Liberation Sans Narrow" font-weight="bold" padding="1mm" space-before="5mm">
                            Legends of the contract
                        </fo:block>

                        <xsl:if test="habitaciones/habitacion or regimenes/regimen">

                        <fo:list-block font-size="7.7pt" font-family="Liberation Sans Narrow" provisional-distance-between-starts="10mm" provisional-label-separation="1mm">

                            <xsl:for-each select="habitaciones/habitacion">
                                <fo:list-item  padding="1mm">
                                    <fo:list-item-label end-indent="label-end()">
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="@id"></xsl:value-of>
                                        </fo:block>
                                    </fo:list-item-label>
                                    <fo:list-item-body start-indent="body-start()">
                                        <fo:block>
                                            <xsl:value-of select="@nombre"></xsl:value-of>
                                        </fo:block>
                                    </fo:list-item-body>
                                </fo:list-item>
                            </xsl:for-each>

                            <xsl:for-each select="regimenes/regimen">
                                <fo:list-item  padding="1mm">
                                    <fo:list-item-label end-indent="label-end()">
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="@codigo"></xsl:value-of>
                                        </fo:block>
                                    </fo:list-item-label>
                                    <fo:list-item-body start-indent="body-start()">
                                        <fo:block>
                                            <xsl:value-of select="@nombre"></xsl:value-of>
                                        </fo:block>
                                    </fo:list-item-body>
                                </fo:list-item>
                            </xsl:for-each>

                        </fo:list-block>

                        </xsl:if>

                        <!-- Delegaciones -->
                        <fo:block font-size="7.7pt" font-family="Liberation Sans Narrow" font-weight="bold" space-before="5mm" space-after="1mm">Offices</fo:block>

                        <xsl:if test="delegaciones/delegacion">

                            <fo:table>
                                <fo:table-body font-size="7.7pt" font-family="Liberation Sans Narrow" space-before="5mm">

                                    <xsl:for-each select="delegaciones/delegacion">

                                        <fo:table-row>
                                            <fo:table-cell padding-before="4mm">
                                                <fo:block page-break-inside="avoid">
                                                    <fo:block font-weight="bold"><xsl:value-of select="@nombre"/></fo:block>
                                                    <fo:block>Tel <xsl:value-of select="@tel"/></fo:block>
                                                    <fo:block>Fax <xsl:value-of select="@fax"/></fo:block>
                                                    <fo:block><xsl:value-of select="@email"/></fo:block>
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>

                                </fo:table-body>
                            </fo:table>

                        </xsl:if>

                        <fo:block id="end"></fo:block>

                    </fo:flow>
                </fo:page-sequence>



            </xsl:for-each>

        </fo:root>
    </xsl:template>
    <xsl:template match="*"/>
</xsl:stylesheet>
