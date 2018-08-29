<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="1.0">
    <xsl:output indent="yes"/>
    <xsl:template match="/root">
        <fo:root>
            <fo:layout-master-set>

                <!-- A4 size -->
                <fo:simple-page-master master-name="dinA4" page-height="29.7cm" page-width="21cm" margin="10mm 20mm 26mm 20mm">
                    <fo:region-body margin-top="51mm" column-count="3"/>
                    <fo:region-before display-align="after"/>
                    <fo:region-after display-align="after"/>
                </fo:simple-page-master>

            </fo:layout-master-set>

            <xsl:for-each select="group">

                <fo:page-sequence master-reference="dinA4">

                    <!-- Header -->
                    <fo:static-content flow-name="xsl-region-before">
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column column-width="74mm"></fo:table-column>
                            <fo:table-column column-width="100mm" border-right-style="solid"
                                             border-right-width="1px"></fo:table-column>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell text-align="left" font-size="20pt" font-weight="bold" padding="1mm">
                                        <fo:block>
                                            <xsl:value-of select="/root/@businessName"></xsl:value-of>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="9pt" font-weight="bold" padding="1mm">
                                        <fo:block>
                                            <fo:inline font-weight="normal">from: </fo:inline><xsl:value-of select="@date"></xsl:value-of>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline color="white">x</fo:inline>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline font-weight="normal">direction: </fo:inline><xsl:value-of select="@direction"/>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline color="white">x</fo:inline>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline font-weight="normal">transfer type: </fo:inline><xsl:value-of select="@type"/>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline color="white">x</fo:inline>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline font-weight="normal">total pax: </fo:inline><xsl:value-of select="@totalPax"/>
                                        </fo:block>
                                        <fo:block>
                                            <fo:inline color="white">x</fo:inline>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                        <fo:block>
                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                                <fo:table-column column-width="174mm"></fo:table-column>
                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm">
                                            <fo:table font-family="Liberation Sans Narrow"
                                                      border-collapse="separate">
                                                <fo:table-column column-width="3.7cm"></fo:table-column>
                                                <fo:table-column column-width="3cm"></fo:table-column>
                                                <fo:table-column column-width="3cm"></fo:table-column>
                                                <fo:table-column column-width="1cm"></fo:table-column>
                                                <fo:table-column column-width="1.3cm"></fo:table-column>
                                                <fo:table-column column-width="3cm"></fo:table-column>
                                                <fo:table-column column-width="3cm"></fo:table-column>
                                                <fo:table-body>
                                                    <fo:table-row>
                                                        <fo:table-cell padding="1mm">
                                                            <fo:block color="white">x</fo:block>
                                                        </fo:table-cell>
                                                    </fo:table-row>                                    <!--                                    <fo:table-row>                                        <fo:table-cell number-columns-spanned="1" text-align="right" font-size="12pt" padding="1mm" font-weight="bold">                                            <fo:block><xsl:value-of select="@id"/></fo:block>                                        </fo:table-cell>                                        <fo:table-cell number-columns-spanned="7" text-align="left" font-size="8pt" padding="1mm">                                            <fo:block><fo:inline font-weight="bold"><xsl:value-of select="@nombre"/></fo:inline> - Prices x <fo:inline font-weight="bold"><xsl:value-of select="@preciopor"/></fo:inline> / night. <xsl:if test="@noreembolsable"><fo:inline font-weight="bold">NON REFUNDABLE</fo:inline></xsl:if></fo:block>                                            <fo:block><xsl:value-of select="@capacidad"/></fo:block>                                        </fo:table-cell>                                    </fo:table-row>                                    -->
                                                    <fo:table-row>
                                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm"
                                                                       font-weight="bold" display-align="after" border-after-style="solid" border-after-width="1px">
                                                            <fo:block>ref.</fo:block>
                                                            <fo:block>agency</fo:block>
                                                            <fo:block>provider</fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm"
                                                                       font-weight="bold" display-align="after" border-after-style="solid" border-after-width="1px">
                                                            <fo:block>name</fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm"
                                                                       font-weight="bold" display-align="after" border-after-style="solid" border-after-width="1px">
                                                            <fo:block>to/from</fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm"
                                                                       font-weight="bold" display-align="after" border-after-style="solid" border-after-width="1px">
                                                            <fo:block>pax</fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm"
                                                                       font-weight="bold" display-align="after" border-after-style="solid" border-after-width="1px">
                                                            <fo:block>pickup</fo:block>
                                                            <fo:block>/ flight</fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm"
                                                                       font-weight="bold" display-align="after" border-after-style="solid" border-after-width="1px">
                                                            <fo:block>flight details</fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell text-align="right" font-size="8pt" padding="1mm"
                                                                       font-weight="bold" display-align="after" border-after-style="solid" border-after-width="1px">
                                                            <fo:block>comments</fo:block>
                                                        </fo:table-cell>

                                                    </fo:table-row>
                                                </fo:table-body>
                                            </fo:table>
                                        </fo:table-cell>
                                    </fo:table-row></fo:table-body></fo:table></fo:block>
                    </fo:static-content>

                    <!-- Footer -->
                    <fo:static-content flow-name="xsl-region-after">
                        <fo:block color="white">x</fo:block>
                        <fo:block text-align="right" font-size="8pt" font-family="Liberation Sans Narrow"><fo:inline padding-right="2cm"><xsl:value-of select="/root/@time"/></fo:inline> Page <fo:page-number></fo:page-number> of
                            <fo:page-number-citation-last ref-id="end"/>.
                        </fo:block>
                    </fo:static-content>

                    <!-- Content -->
                    <fo:flow flow-name="xsl-region-body">

                        <fo:block span="all">

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
                                                    <fo:table-column column-width="3.7cm"></fo:table-column>
                                                    <fo:table-column column-width="3cm"></fo:table-column>
                                                    <fo:table-column column-width="3cm"></fo:table-column>
                                                    <fo:table-column column-width="1cm"></fo:table-column>
                                                    <fo:table-column column-width="1.3cm"></fo:table-column>
                                                    <fo:table-column column-width="3cm"></fo:table-column>
                                                    <fo:table-column column-width="3cm"></fo:table-column>
                                                    <fo:table-body>
                                                        <xsl:for-each select="service">
                                                            <fo:table-row keep-together.within-page="always">
                                                                <xsl:variable name="pos" select="position()"></xsl:variable>
                                                                <fo:table-cell text-align="right" font-size="8pt"
                                                                               padding="1mm"
                                                                               border-right-style="solid"
                                                                               border-right-width="0.3px" border-after-style="solid" border-after-width="0.3px">
                                                                    <fo:block>
                                                                        <xsl:value-of select="@agencyReference"/>
                                                                    </fo:block>
                                                                    <fo:block>
                                                                        <xsl:value-of select="@agency"/>
                                                                    </fo:block>
                                                                    <fo:block>
                                                                        <xsl:value-of select="@providers"/>
                                                                    </fo:block>
                                                                </fo:table-cell>
                                                                <fo:table-cell text-align="right" font-size="8pt"
                                                                               padding="1mm"
                                                                               border-right-style="solid"
                                                                               border-right-width="0.3px" border-after-style="solid" border-after-width="0.3px">
                                                                    <fo:block>
                                                                        <xsl:value-of select="@leadName"/>
                                                                    </fo:block>
                                                                </fo:table-cell>
                                                                <fo:table-cell text-align="right" font-size="8pt"
                                                                               padding="1mm"
                                                                               border-right-style="solid"
                                                                               border-right-width="0.3px" border-after-style="solid" border-after-width="0.3px">
                                                                    <fo:block>
                                                                        <xsl:choose>
                                                                            <xsl:when test="@direction = 'POINTTOPOINT'"><xsl:value-of select="@pickup"/> >>> TO >>> <xsl:value-of select="@dropoff"/></xsl:when>
                                                                            <xsl:when test="@direction = 'INBOUND'"><xsl:value-of select="@dropoff"/></xsl:when>
                                                                            <xsl:otherwise><xsl:value-of select="@pickup"/></xsl:otherwise>
                                                                        </xsl:choose>
                                                                    </fo:block>
                                                                    <fo:block>
                                                                        <xsl:choose>
                                                                            <xsl:when test="@direction = 'INBOUND'"><xsl:value-of select="@dropoffResort"/></xsl:when>
                                                                            <xsl:otherwise><xsl:value-of select="@pickupResort"/></xsl:otherwise>
                                                                        </xsl:choose>
                                                                    </fo:block>
                                                                    <xsl:if test="@alternatePickup">
                                                                        <fo:block font-style="italic">
                                                                            <xsl:value-of select="@alternatePickup"/>
                                                                        </fo:block>
                                                                    </xsl:if>
                                                                </fo:table-cell>
                                                                <fo:table-cell text-align="right" font-size="8pt"
                                                                               padding="1mm"
                                                                               border-right-style="solid"
                                                                               border-right-width="0.3px" border-after-style="solid" border-after-width="0.3px">
                                                                    <fo:block>
                                                                        <xsl:value-of select="@pax"/>
                                                                    </fo:block>
                                                                </fo:table-cell>
                                                                <fo:table-cell text-align="right" font-size="8pt"
                                                                               padding="1mm"
                                                                               border-right-style="solid"
                                                                               border-right-width="0.3px" border-after-style="solid" border-after-width="0.3px">
                                                                    <fo:block>
                                                                        <xsl:value-of select="@pickupTime"/>
                                                                    </fo:block>
                                                                    <fo:block>
                                                                        /<xsl:value-of select="@flightTime"/>
                                                                    </fo:block>
                                                                </fo:table-cell>
                                                                <fo:table-cell text-align="right" font-size="8pt"
                                                                               padding="1mm"
                                                                               border-right-style="solid"
                                                                               border-right-width="0.3px" border-after-style="solid" border-after-width="0.3px">
                                                                    <fo:block>
                                                                        <xsl:value-of select="@flight"/>
                                                                    </fo:block>
                                                                    <fo:block>
                                                                        <xsl:value-of select="@flightOriginOrDestination"/>
                                                                    </fo:block>
                                                                </fo:table-cell>
                                                                <fo:table-cell text-align="right" font-size="8pt"
                                                                               padding="1mm"
                                                                               border-right-style="solid"
                                                                               border-right-width="0.3px" border-after-style="solid" border-after-width="0.3px">
                                                                    <fo:block>
                                                                        <xsl:value-of select="@comments"/>
                                                                    </fo:block>
                                                                    <fo:block>
                                                                        <xsl:value-of select="@preferredVehicle"/>
                                                                    </fo:block>
                                                                </fo:table-cell>
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

                        <!-- FIN -->

                        <fo:block id="end"></fo:block>
                    </fo:flow>
                </fo:page-sequence>

            </xsl:for-each>

        </fo:root>
    </xsl:template>
    <xsl:template match="*"/>
</xsl:stylesheet>