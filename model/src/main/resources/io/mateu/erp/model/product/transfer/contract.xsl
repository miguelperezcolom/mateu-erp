<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
    <xsl:output indent="yes"/>
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>

                <!-- A4 size -->
                <fo:simple-page-master master-name="dinA4" page-height="29.7cm" page-width="21cm" margin="2mm 20mm 26mm 20mm">
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
                            <fo:table-column column-width="60mm" border-right-style="solid"
                                             border-right-width="0.3px"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid"
                                             border-right-width="0.3px"></fo:table-column>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="left"
                                                   font-size="8pt">                                        <!--                                        <fo:block><fo:external-graphic src="{nosotros/@logo}" content-height="scale-to-fit" height="13.3mm" content-width="scale-to-fit" width="31.2mm"/></fo:block>                                        -->
                                        <xsl:for-each select="target">
                                            <fo:block>
                                                <xsl:value-of select="@name"></xsl:value-of>
                                            </fo:block>
                                        </xsl:for-each>
                                        <fo:block>
                                            <fo:inline color="white">x</fo:inline>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="9pt" font-weight="bold" padding="1mm">
                                        <fo:block>
                                            <xsl:value-of select="@title"></xsl:value-of>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline color="white">x</fo:inline>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="@type"/>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline color="white">x</fo:inline>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="@transferType"/>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline color="white">x</fo:inline>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="@billingConcept"/>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline color="white">x</fo:inline>
                                        </fo:block>
                                        <fo:block>VAT
                                            <xsl:value-of select="@vat"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="8pt" padding="1mm">
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="supplier/@name"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="supplier/@businessName"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="supplier/@address"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="supplier/@vaiIdentificationNumber"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="supplier/@email"/>
                                        </fo:block>                                        <!--                                        <fo:block font-weight="bold"><xsl:value-of select="objeto/@nombre"/> <xsl:value-of select="objeto/@categoria"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@nombrefiscal"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@cif"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@direccion"/></fo:block>                                        <xsl:if test="objeto/@poblacion"><fo:block><xsl:value-of select="objeto/@poblacion"/></fo:block></xsl:if>                                        <xsl:if test="objeto/@provincia"><fo:block><xsl:value-of select="objeto/@provincia"/></fo:block></xsl:if>                                        <xsl:if test="objeto/@pais"><fo:block><xsl:value-of select="objeto/@pais"/></fo:block></xsl:if>                                        <fo:block>t:<xsl:value-of select="objeto/@telefono"/></fo:block>                                        <fo:block>f:<xsl:value-of select="objeto/@fax"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@email"/></fo:block>                                        -->
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                    </fo:static-content>

                    <!-- Footer -->
                    <fo:static-content flow-name="xsl-region-after">
                        <fo:block color="white">x</fo:block>
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column column-width="54mm" border-right-style="solid"
                                             border-right-width="0.3px"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid"
                                             border-right-width="0.3px"></fo:table-column>
                            <fo:table-column column-width="60mm" border-right-style="solid"
                                             border-right-width="0.3px"></fo:table-column>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="right" font-size="8pt" padding="1mm"
                                                   display-align="after">
                                        <fo:block>And as proof of consent and acceptance of all the clauses , including
                                            those contained in page<fo:page-number-citation-last ref-id="end"/>, the two
                                            sides signed in <xsl:value-of select="@firmadoen"/>,
                                            <xsl:value-of select="@fecha"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="8pt" font-weight="bold" padding="1mm"
                                                   display-align="after">
                                        <fo:block>Name and surname</fo:block>
                                        <fo:block>By
                                            <xsl:value-of select="entre/@nombre"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="8pt" font-weight="bold" padding="1mm"
                                                   display-align="after">
                                        <fo:block>Name and surname</fo:block>
                                        <fo:block>By
                                            <xsl:value-of select="y/@nombre"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                        <fo:block text-align="right" font-size="8pt" font-family="Liberation Sans Narrow">Page <fo:page-number></fo:page-number> of
                            <fo:page-number-citation-last ref-id="end"/>
                        </fo:block>
                    </fo:static-content>

                    <!-- Content -->
                    <fo:flow flow-name="xsl-region-body">

                        <fo:block span="all">

                        <!-- Acuerdo comercial, 1 y 2 -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column column-width="174mm"></fo:table-column>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell font-size="8pt" padding="1mm">
                                        <fo:block>
                                            <fo:block>For transfers from
                                                <xsl:value-of select="@validFrom"/> to <xsl:value-of select="@validTo"/>.
                                            </fo:block>
                                            <xsl:choose>
                                                <xsl:when test="target">For
                                                    <xsl:for-each select="target">
                                                        <xsl:if test="position() > 1">,</xsl:if>
                                                        <xsl:value-of select="@name"/>
                                                    </xsl:for-each>
                                                    .
                                                </xsl:when>
                                                <xsl:otherwise>Valid for all markets</xsl:otherwise>
                                            </xsl:choose>
                                        </fo:block>
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
                                    <fo:table-cell font-size="8pt" padding="1mm">
                                        <xsl:choose>
                                            <xsl:when test="@specialTerms">
                                                <fo:block>
                                                    <xsl:value-of select="@specialTerms"/>
                                                </fo:block>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <fo:block></fo:block>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt"></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                        <!-- PRECIOS -->
                        <!-- <fo:block page-break-inside="avoid"> -->
                        <fo:block>
                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                                <fo:table-column column-width="174mm"></fo:table-column>
                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm">
                                            <fo:table font-family="Liberation Sans Narrow"
                                                      border-collapse="separate">
                                                <fo:table-column column-width="3cm"></fo:table-column>
                                                <fo:table-column column-width="3cm"></fo:table-column>
                                                <xsl:for-each select="vehicle">
                                                    <fo:table-column column-width="2cm"></fo:table-column>
                                                </xsl:for-each>
                                                <fo:table-body>
                                                    <fo:table-row>
                                                        <fo:table-cell padding="1mm">
                                                            <fo:block color="white">x</fo:block>
                                                        </fo:table-cell>
                                                    </fo:table-row>                                    <!--                                    <fo:table-row>                                        <fo:table-cell number-columns-spanned="1" text-align="right" font-size="12pt" padding="1mm" font-weight="bold">                                            <fo:block><xsl:value-of select="@id"/></fo:block>                                        </fo:table-cell>                                        <fo:table-cell number-columns-spanned="7" text-align="left" font-size="8pt" padding="1mm">                                            <fo:block><fo:inline font-weight="bold"><xsl:value-of select="@nombre"/></fo:inline> - Prices x <fo:inline font-weight="bold"><xsl:value-of select="@preciopor"/></fo:inline> / night. <xsl:if test="@noreembolsable"><fo:inline font-weight="bold">NON REFUNDABLE</fo:inline></xsl:if></fo:block>                                            <fo:block><xsl:value-of select="@capacidad"/></fo:block>                                        </fo:table-cell>                                    </fo:table-row>                                    -->
                                                    <fo:table-row>
                                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm"
                                                                       font-weight="bold" display-align="after">
                                                            <fo:block>between</fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm"
                                                                       font-weight="bold" display-align="after">
                                                            <fo:block>and</fo:block>
                                                        </fo:table-cell>
                                                        <xsl:for-each select="vehicle">
                                                            <fo:table-cell text-align="right" font-size="8pt"
                                                                           padding="1mm" font-weight="bold"
                                                                           display-align="after">
                                                                <fo:block>
                                                                    <xsl:value-of select="@name"/>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                        </xsl:for-each>
                                                    </fo:table-row>
                                                    <xsl:for-each select="line">
                                                        <fo:table-row>
                                                            <xsl:variable name="pos" select="position()"></xsl:variable>
                                                            <fo:table-cell text-align="right" font-size="8pt"
                                                                           padding="1mm" font-weight="bold"
                                                                           border-right-style="solid"
                                                                           border-right-width="0.3px">
                                                                <xsl:if test="$pos = 1">
                                                                    <xsl:attribute name="border-before-style">solid
                                                                    </xsl:attribute>
                                                                    <xsl:attribute name="border-before-width">0.3px
                                                                    </xsl:attribute>
                                                                </xsl:if>
                                                                <fo:block>
                                                                    <xsl:value-of select="@origin"/>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <fo:table-cell text-align="right" font-size="8pt"
                                                                           padding="1mm" font-weight="bold"
                                                                           border-right-style="solid"
                                                                           border-right-width="0.3px">
                                                                <xsl:if test="$pos = 1">
                                                                    <xsl:attribute name="border-before-style">solid
                                                                    </xsl:attribute>
                                                                    <xsl:attribute name="border-before-width">0.3px
                                                                    </xsl:attribute>
                                                                </xsl:if>
                                                                <fo:block>
                                                                    <xsl:value-of select="@destination"/>
                                                                </fo:block>
                                                            </fo:table-cell>
                                                            <xsl:for-each select="price">
                                                                <fo:table-cell text-align="right" font-size="8pt"
                                                                               padding="1mm" border-right-style="solid"
                                                                               border-right-width="0.3px">
                                                                    <xsl:if test="$pos = 1">
                                                                        <xsl:attribute name="border-before-style">
                                                                            solid
                                                                        </xsl:attribute>
                                                                        <xsl:attribute name="border-before-width">
                                                                            0.3px
                                                                        </xsl:attribute>
                                                                    </xsl:if>
                                                                    <fo:block>
                                                                        <xsl:value-of select="@price"/> /
                                                                        <xsl:value-of select="@per"/>
                                                                    </fo:block>
                                                                </fo:table-cell>
                                                            </xsl:for-each>
                                                        </fo:table-row>
                                                    </xsl:for-each>
                                                </fo:table-body>
                                            </fo:table>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>
                        </fo:block>


                        </fo:block>




                        <!-- LEYENDA ZONAS -->

                        <xsl:if test="zone">
                            <fo:block>
                                <fo:table>
                                    <fo:table-body font-size="7.7pt" font-family="Liberation Sans Narrow"
                                                   space-before="5mm">
                                        <xsl:for-each select="zone">
                                            <fo:table-row>
                                                <fo:table-cell padding-before="4mm">
                                                    <fo:block page-break-inside="avoid">
                                                        <fo:block font-weight="bold">
                                                            <xsl:value-of select="@name"/>
                                                        </fo:block>
                                                        <xsl:for-each select="city">
                                                            <fo:block>
                                                                <xsl:value-of select="@name"/>
                                                            </fo:block>
                                                        </xsl:for-each>
                                                        <xsl:for-each select="point">
                                                            <fo:block>
                                                                <xsl:value-of select="@name"/>
                                                            </fo:block>
                                                        </xsl:for-each>                                                    <!--                                                    <fo:block>Tel <xsl:value-of select="@tel"/></fo:block>                                                    <fo:block>Fax <xsl:value-of select="@fax"/></fo:block>                                                    <fo:block><xsl:value-of select="@email"/></fo:block>                                                    -->
                                                    </fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </xsl:for-each>
                                    </fo:table-body>
                                </fo:table>
                            </fo:block>
                        </xsl:if>



                        <!-- FIN -->

                        <fo:block id="end"></fo:block>
                    </fo:flow>
                </fo:page-sequence>
            </xsl:for-each>
        </fo:root>
    </xsl:template>
    <xsl:template match="*"/>
</xsl:stylesheet>