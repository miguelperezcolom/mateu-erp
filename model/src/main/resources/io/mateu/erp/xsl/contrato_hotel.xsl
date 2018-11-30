<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output indent="yes"/>
    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>

                <!-- A4 size -->
                <fo:simple-page-master master-name="dinA4" page-height="29.7cm" page-width="21cm" margin="20mm 20mm 32mm 20mm">
                    <fo:region-body margin-top="46mm"/>
                    <fo:region-before display-align="after"/>
                    <fo:region-after display-align="after"/>
                </fo:simple-page-master>

                <fo:simple-page-master master-name="last" page-height="29.7cm" page-width="21cm" margin="15mm 20mm 32mm 20mm">
                    <fo:region-body margin-top="46mm" column-count="3"/>
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
                                                   font-size="10pt">
                                        <xsl:for-each select="office/company">
                                            <fo:block><fo:external-graphic src="{@logo}" content-height="scale-to-fit" height="13.3mm" content-width="scale-to-fit" width="31.2mm"/></fo:block>
                                            <fo:block font-weight="bold">
                                                <xsl:value-of select="@name"/>
                                            </fo:block>
                                            <fo:block>
                                                <xsl:value-of select="@businessName"/>
                                            </fo:block>
                                            <fo:block>
                                                <xsl:value-of select="@address"/>
                                            </fo:block>
                                            <fo:block>
                                                <xsl:value-of select="@vatIdentificationNumber"/>
                                            </fo:block>
                                            <fo:block>
                                                t. <xsl:value-of select="@telephone"/>
                                            </fo:block>
                                            <fo:block>
                                                <xsl:value-of select="@email"/>
                                            </fo:block>
                                        </xsl:for-each>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="9pt" font-weight="bold" padding="1mm">
                                        <fo:block >
                                            <xsl:value-of select="@title"></xsl:value-of>
                                        </fo:block>
                                        <fo:block margin-top="2mm">
                                            <xsl:value-of select="@type"/>
                                        </fo:block>
                                        <xsl:if test="warranty">
                                            <fo:block margin-top="2mm">
                                                WARRANTY
                                            </fo:block>
                                        </xsl:if>
                                        <fo:block margin-top="2mm">
                                            <xsl:value-of select="@currencyCode"/>
                                        </fo:block>
                                        <fo:block margin-top="2mm">VAT
                                            <xsl:value-of select="@vat"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm">
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="hotel/@name"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="hotel/@category"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="hotel/@address"/>
                                        </fo:block>
                                        <fo:block>
                                            t. <xsl:value-of select="hotel/@telephone"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="hotel/@fax"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="hotel/@email"/>
                                        </fo:block>
                                                                                <!--                                        <fo:block font-weight="bold"><xsl:value-of select="objeto/@nombre"/> <xsl:value-of select="objeto/@categoria"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@nombrefiscal"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@cif"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@direccion"/></fo:block>                                        <xsl:if test="objeto/@poblacion"><fo:block><xsl:value-of select="objeto/@poblacion"/></fo:block></xsl:if>                                        <xsl:if test="objeto/@provincia"><fo:block><xsl:value-of select="objeto/@provincia"/></fo:block></xsl:if>                                        <xsl:if test="objeto/@pais"><fo:block><xsl:value-of select="objeto/@pais"/></fo:block></xsl:if>                                        <fo:block>t:<xsl:value-of select="objeto/@telefono"/></fo:block>                                        <fo:block>f:<xsl:value-of select="objeto/@fax"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@email"/></fo:block>                                        -->
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
                                    <fo:table-cell font-size="10pt" padding="1mm"
                                                   display-align="after">
                                        <fo:block>And as proof of consent and acceptance of all the clauses , including
                                            those contained in page <fo:page-number-citation-last ref-id="end"/>, the two
                                            sides signed in <xsl:value-of select="@signedAt"/>,
                                            <xsl:value-of select="@signatureDate"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="10pt" font-weight="bold" padding="1mm"
                                                   display-align="after">
                                        <fo:block>Name and surname</fo:block>
                                        <fo:block>By
                                            <xsl:value-of select="@partnerSignatory"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="10pt" font-weight="bold" padding="1mm"
                                                   display-align="after">
                                        <fo:block>Name and surname</fo:block>
                                        <fo:block>By
                                            <xsl:value-of select="@ownSignatory"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                        <fo:block text-align="right" font-size="10pt" font-family="Liberation Sans Narrow">Page <fo:page-number></fo:page-number> of
                            <fo:page-number-citation-last ref-id="end"/>
                        </fo:block>
                    </fo:static-content>

                    <!-- Content -->
                    <fo:flow flow-name="xsl-region-body" >



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
                                        <fo:block font-weight="700" font-size="16pt">COMMERCIAL AGREEMENT <!--  <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Release</fo:inline> --></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell font-size="10pt" margin-left="0mm" padding="1mm">
                                        <fo:block>

                                            The parties gathered <fo:inline font-weight="700"><xsl:value-of select="office/company/@name"/></fo:inline> --
                                        <xsl:value-of select="office/company/@businessName"/>
                                        , <xsl:value-of select="office/company/@vatIdentificationNumber"/>
                                        , <xsl:value-of select="office/company/@address"/>
                                            , <xsl:value-of select="office/company/@postalCode"/>
                                            , <xsl:value-of select="office/company/@city"/>
                                            , <xsl:value-of select="office/company/@state"/>
                                            , <xsl:value-of select="office/company/@country"/>
                                            -- (THE CUSTOMER) and <fo:inline font-weight="700"><xsl:value-of select="partner/@name"/></fo:inline>
                                        --
                                        <xsl:value-of select="partner/@businessName"/>
                                        , <xsl:value-of select="partner/@vatIdentificationNumber"/>
                                        , <xsl:value-of select="partner/@address"/>
                                            , <xsl:value-of select="partner/@postalCode"/>
                                            , <xsl:value-of select="partner/@city"/>
                                            , <xsl:value-of select="partner/@state"/>
                                            , <xsl:value-of select="partner/@country"/>
                                            , <xsl:value-of select="partner/@email"/>
                                        -- (THE SUPPLIER) agree to the following:

                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                            </fo:table-body>
                        </fo:table>



                        </fo:block>

                        <!-- Acuerdo comercial, 1 y 2 -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column column-width="174mm"></fo:table-column>
                            <fo:table-body>

                                <fo:table-row>
                                    <fo:table-cell padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt"></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt">1. Application <!--  <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Release</fo:inline> --></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>


                                <fo:table-row>
                                    <fo:table-cell font-size="10pt" padding="1mm">
                                        <fo:block>
                                            <fo:block>This contract is valid for services from
                                                <xsl:value-of select="@validFrom"/> to <xsl:value-of select="@validTo"/> (last checkout date).
                                            </fo:block>
                                            <fo:block>
                                                <fo:inline color="white">x</fo:inline>
                                            </fo:block>
                                            <xsl:choose>
                                                <xsl:when test="targets/target">For
                                                    <xsl:for-each select="targets/target">
                                                        <xsl:if test="position() > 1">,</xsl:if>
                                                        <xsl:value-of select="@name"/>
                                                    </xsl:for-each>
                                                    .
                                                </xsl:when>
                                                <xsl:otherwise>Valid for all markets.</xsl:otherwise>
                                            </xsl:choose>
                                            <xsl:if test="bannedTargets/target">
                                            <fo:block>
                                                <fo:inline color="white">x</fo:inline>
                                            </fo:block>
                                                Not for
                                                    <xsl:for-each select="bannedTargets/target">
                                                        <xsl:if test="position() > 1">,</xsl:if>
                                                        <xsl:value-of select="@name"/>
                                                    </xsl:for-each>
                                                    .
                                            </xsl:if>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt"></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                        </fo:table>



                        <!-- Acuerdo comercial, 1 y 2 -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column column-width="174mm"></fo:table-column>
                            <fo:table-body>

                                <fo:table-row>
                                    <fo:table-cell padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt"></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt">2. Legends <!--  <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Release</fo:inline> --></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>


                                <!-- Leyendas del contrato -->

                                <fo:table-row>
                                    <fo:table-cell padding="1mm">

                                        <fo:block font-size="10pt" font-family="Liberation Sans Narrow" padding="1mm" >
                                            Legends of the contract
                                        </fo:block>


                                        <xsl:if test="rooms/room or boards/board">

                                            <fo:list-block font-size="10pt" font-family="Liberation Sans Narrow" provisional-distance-between-starts="25mm" provisional-label-separation="1mm">

                                                <xsl:for-each select="rooms/room">
                                                    <fo:list-item  padding="1mm">
                                                        <fo:list-item-label end-indent="label-end()">
                                                            <fo:block font-weight="bold">
                                                                <xsl:value-of select="@code"></xsl:value-of>
                                                            </fo:block>
                                                        </fo:list-item-label>
                                                        <fo:list-item-body start-indent="body-start()">
                                                            <fo:block>
                                                                <xsl:value-of select="@name"></xsl:value-of>
                                                            </fo:block>
                                                        </fo:list-item-body>
                                                    </fo:list-item>
                                                </xsl:for-each>

                                                <xsl:for-each select="boards/board">
                                                    <fo:list-item  padding="1mm">
                                                        <fo:list-item-label end-indent="label-end()">
                                                            <fo:block font-weight="bold">
                                                                <xsl:value-of select="@code"></xsl:value-of>
                                                            </fo:block>
                                                        </fo:list-item-label>
                                                        <fo:list-item-body start-indent="body-start()">
                                                            <fo:block>
                                                                <xsl:value-of select="@name"></xsl:value-of>
                                                            </fo:block>
                                                        </fo:list-item-body>
                                                    </fo:list-item>
                                                </xsl:for-each>

                                            </fo:list-block>

                                        </xsl:if>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt"></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                        </fo:table>





                        <!-- Acuerdo comercial, 1 y 2 -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column column-width="174mm"></fo:table-column>
                            <fo:table-body>

                                <fo:table-row>
                                    <fo:table-cell padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt"></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell border-bottom-style="solid" border-right-width="0.3px" padding="1mm">
                                        <fo:block font-weight="700" font-size="12pt">4. Room prices <!--  <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Release</fo:inline> --></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>

                                <fo:table-row>
                                    <fo:table-cell padding="1mm">
                                        <fo:block font-size="10pt">Room types, capacities, dates, boards and rates (<fo:inline font-weight="700"><xsl:value-of select="@ratesType"/></fo:inline> prices, are in <xsl:value-of select="@currencyCode"/>, IVA / IGIC <xsl:value-of select="@vat"/>) , as follows.</fo:block>
                                        <fo:block font-size="10pt">
                                            Supplements / discounts: indicated by + or -, are Pax / night.
                                        </fo:block>
                                        <fo:block font-size="10pt">
                                            Infant until <xsl:value-of select="hotel/@childStartAge"/> years (not included). <xsl:if test="hotel/@juniorStartAge"> Junior from <xsl:value-of select="hotel/@juniorStartAge"/> years (included).</xsl:if> Adult from <xsl:value-of select="hotel/@adultStartAge"/> years (included). Children will be ordered <xsl:choose>
                                            <xsl:when test="hotel/@youngestFirst">from younger to older</xsl:when>
                                            <xsl:otherwise>from older to younger</xsl:otherwise>
                                        </xsl:choose> for discounts application.</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>


                                <xsl:if test="warranty">
                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block font-size="10pt">This is a <fo:inline font-weight="700">WARRANTY</fo:inline> contract.</fo:block>
                                            <fo:block font-size="10pt">Settlement basis is <xsl:value-of select="warranty/@settlementBasis"/>.</fo:block>
                                            <xsl:if test="warranty/@productionWarranty"><fo:block font-size="10pt">This is a PRODUCTION warranty.</fo:block></xsl:if>
                                            <fo:block font-size="10pt">Warranty coverage is <xsl:value-of select="warranty/@warrantyPercent"/> %.</fo:block>
                                            <fo:block font-size="10pt">Extras are <xsl:if test="not(warrant/@extrasIncludedInWarranty)">NOT</xsl:if> included in warranty.</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </xsl:if>




                            </fo:table-body>
                        </fo:table>

                        <xsl:for-each select="terms/rooms/room">

                            <fo:block page-break-inside="avoid">
                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                    <fo:table-column column-width="21mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
                                    <fo:table-column column-width="17mm"></fo:table-column>
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
                                            <fo:table-cell number-columns-spanned="7" text-align="left" font-size="10pt" padding="1mm">
                                                <fo:block><fo:inline font-weight="bold"><xsl:value-of select="@board"/></fo:inline> Prices per night. <xsl:if test="@nonrefundable"><fo:inline font-weight="bold">NON REFUNDABLE</fo:inline></xsl:if></fo:block>
                                                <fo:block><xsl:value-of select="@capacity"/></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>


                                        <xsl:choose>
                                            <xsl:when test="../../../@noPriceDetail"></xsl:when>
                                            <xsl:otherwise>



                                                <xsl:for-each select="row">

                                                    <fo:table-row>
                                                        <fo:table-cell>
                                                            <fo:block></fo:block>
                                                        </fo:table-cell>
                                                        <xsl:for-each select="dates/ranges">
                                                            <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold">
                                                                <xsl:for-each select="range"><fo:block><xsl:if test="position() > 1"><xsl:attribute name="border-before-style">dashed</xsl:attribute><xsl:attribute name="border-before-width">thin</xsl:attribute></xsl:if><xsl:value-of select="@start"/></fo:block><fo:block><xsl:value-of select="@end"/></fo:block></xsl:for-each>
                                                            </fo:table-cell>
                                                        </xsl:for-each>
                                                    </fo:table-row>
                                                    <xsl:for-each select="line[@tipo = 'base']">
                                                        <fo:table-row>
                                                            <xsl:variable name="pos" select="position()"></xsl:variable>
                                                            <fo:table-cell
                                                                    text-align="right"
                                                                    font-size="10pt"
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
                                                                        font-size="10pt"
                                                                        padding="1mm"
                                                                        border-right-style="solid"
                                                                        border-right-width="0.3px">
                                                                    <xsl:if test="$pos = 1"><xsl:attribute name="border-before-style">solid</xsl:attribute><xsl:attribute name="border-before-width">0.3px</xsl:attribute></xsl:if>
                                                                    <fo:block><xsl:value-of select="."/></fo:block>
                                                                </fo:table-cell>
                                                            </xsl:for-each>
                                                        </fo:table-row>
                                                    </xsl:for-each>
                                                    <xsl:for-each select="line[@tipo = 'suplemento']">
                                                        <xsl:variable name="pos" select="position()"></xsl:variable>
                                                        <fo:table-row>
                                                            <fo:table-cell
                                                                    text-align="right"
                                                                    font-size="10pt"
                                                                    padding="1mm"
                                                                    border-right-style="solid"
                                                                    border-right-width="0.3px">
                                                                <fo:block><xsl:value-of select="@description"/></fo:block>
                                                            </fo:table-cell>
                                                            <xsl:for-each select="price">
                                                                <fo:table-cell
                                                                        text-align="right"
                                                                        font-size="10pt"
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

                                            </xsl:otherwise>
                                        </xsl:choose>

                                    </fo:table-body>

                                </fo:table>
                            </fo:block>
                        </xsl:for-each>


                        <!-- 3. Release -->

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
                                                <fo:block font-weight="700" font-size="12pt">5. Release <!--  <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Release</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <fo:table-row>
                                            <fo:table-cell font-size="10pt" padding="1mm">
                                                <fo:block>
                                                    If not stated below release 0 will be applied.
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                    </fo:table-body>
                                </fo:table>


                                <xsl:choose>
                                    <xsl:when test="terms/releases/rule">

                                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                            <fo:table-column column-width="20mm"></fo:table-column>
                                            <fo:table-column column-width="20mm"></fo:table-column>
                                            <fo:table-column column-width="20mm"></fo:table-column>

                                            <fo:table-header>
                                                <fo:table-row>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold">
                                                        <fo:block>from</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold">
                                                        <fo:block>to</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold">
                                                        <fo:block>release</fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-header>

                                            <fo:table-body>
                                                <fo:table-row>
                                                    <fo:table-cell
                                                            text-align="right"
                                                            font-size="10pt"
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
                                                            font-size="10pt"
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
                                                            font-size="10pt"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="0.3px"
                                                            border-before-style="solid"
                                                            border-before-width="0.3px">
                                                        <xsl:for-each select="terms/releases/rule">
                                                            <fo:block><xsl:value-of select="@release"/></fo:block>
                                                        </xsl:for-each>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-body>
                                        </fo:table>


                                    </xsl:when>

                                    <xsl:otherwise>
                                        <fo:block font-size="10pt" margin-top="1mm" margin-left="1mm">Do not apply.</fo:block>
                                    </xsl:otherwise>
                                </xsl:choose>


                            </fo:block>
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
                                            <fo:block font-weight="700" font-size="12pt">6. Allotment <!-- <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Cupo</fo:inline> --></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:if test="@aceptaonrequest">
                                        <fo:table-row>
                                            <fo:table-cell font-size="10pt" margin-left="54mm" padding="1mm">
                                                <fo:block>
                                                    When no allotment is available files are allowed ON REQUEST.
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:if>


                                </fo:table-body>
                            </fo:table>

                            <xsl:choose>
                                <xsl:when test="terms/allotment/allotment">


                                    <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                        <fo:table-column column-width="20mm"></fo:table-column>
                                        <fo:table-column column-width="20mm"></fo:table-column>
                                        <fo:table-column column-width="20mm"></fo:table-column>
                                        <fo:table-column column-width="20mm"></fo:table-column>

                                        <fo:table-header>
                                            <fo:table-row>
                                                <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                    <fo:block>room</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                    <fo:block>from</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                    <fo:block>to</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                    <fo:block>allotment</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-header>

                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="10pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <xsl:for-each select="terms/allotment/allotment">
                                                        <fo:block><xsl:value-of select="@room"/></fo:block>
                                                    </xsl:for-each>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="10pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <xsl:for-each select="terms/allotment/allotment">
                                                        <fo:block><xsl:value-of select="@start"/></fo:block>
                                                    </xsl:for-each>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="10pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px">
                                                    <xsl:for-each select="terms/allotment/allotment">
                                                        <fo:block><xsl:value-of select="@end"/></fo:block>
                                                    </xsl:for-each>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="10pt"
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



                                </xsl:when>
                                <xsl:otherwise>
                                    <fo:block font-size="10pt" margin-top="1mm" margin-left="1mm">Do not apply.</fo:block>
                                </xsl:otherwise>
                            </xsl:choose>


                        </fo:block>
                        <xsl:if test="terms/securityAllotment/allotment">

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
                                                <fo:block font-weight="700" font-size="12pt">6.1. Security allotment <!--  <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Cupo garantizado</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                        <fo:table-row>
                                            <fo:table-cell font-size="10pt" padding="1mm">
                                                <fo:block>
                                                    This is SECURITY allotment (activated when stop sales applied)
                                                    <!--
                                                    <xsl:choose>
                                                        <xsl:when test="cupogarantizado/@seguridad">This is SECURITY allotment (activated when stop sales applied)</xsl:when>
                                                        <xsl:otherwise>This is MINIMUM allotment (activated when stop sales applied, but affected by existent files)</xsl:otherwise>
                                                    </xsl:choose>
                                                    -->
                                                </fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>

                                    </fo:table-body>
                                </fo:table>

                                <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>
                                    <fo:table-column column-width="20mm"></fo:table-column>

                                    <fo:table-header>
                                        <fo:table-row>
                                            <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>room</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>from</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>to</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                <fo:block>allotment</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-header>

                                    <fo:table-body>
                                        <fo:table-row>

                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="10pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/securityAllotment/allotment">
                                                    <fo:block><xsl:value-of select="@room"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="10pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/securityAllotment/allotment">
                                                    <fo:block><xsl:value-of select="@start"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="10pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/securityAllotment/allotment">
                                                    <fo:block><xsl:value-of select="@end"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="10pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <xsl:for-each select="terms/securityAllotment/allotment">
                                                    <fo:block><xsl:value-of select="@quantity"/></fo:block>
                                                </xsl:for-each>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>
                            </fo:block>


                        </xsl:if>

                        <!-- 6. Suplementos -->

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
                                            <fo:block font-weight="700" font-size="12pt">7. Supplements <!-- <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Suplementos</fo:inline>--></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>


                            <xsl:choose>
                            <xsl:when test="terms/supplements/supplement">

                            <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                <fo:table-column column-width="17mm"></fo:table-column>
                                <fo:table-column column-width="17mm"></fo:table-column>
                                <fo:table-column column-width="140mm"></fo:table-column>

                                <fo:table-header>
                                    <fo:table-row display-align="after">
                                        <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                            <fo:block>From</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                            <fo:block>to</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                            <fo:block>description</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-header>

                                <fo:table-body>

                                    <xsl:for-each select="terms/supplements/supplement">


                                        <fo:table-row>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="10pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <fo:block><xsl:value-of select="@start"/></fo:block>
                                            </fo:table-cell>

                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="10pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <fo:block><xsl:value-of select="@end"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    font-size="10pt"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.3px">
                                                <fo:block><xsl:value-of select="@descriptionforpdf"/></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>
                                </fo:table-body>
                            </fo:table>

                            </xsl:when>
                                <xsl:otherwise><fo:block font-size="10pt" margin-top="1mm" margin-left="1mm">Do not apply.</fo:block></xsl:otherwise>
                            </xsl:choose>

                        </fo:block>
                        <!-- 6. Estáncias mínimas -->

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
                                                <fo:block font-weight="700" font-size="12pt">8. Minimum stay <!-- <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Estáncias mínimas</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                        <fo:table-row>
                                            <fo:table-cell font-size="10pt" padding="1mm">
                                                <fo:block>The conditions apply when booking for a number of nights fewer than indicated</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>

                                <xsl:choose>
                                    <xsl:when test="terms/minimumStays/rule">

                                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                            <fo:table-column column-width="20mm"></fo:table-column>
                                            <fo:table-column column-width="20mm"></fo:table-column>
                                            <fo:table-column column-width="20mm"></fo:table-column>
                                            <fo:table-column column-width="114mm"></fo:table-column>

                                            <fo:table-header>
                                                <fo:table-row display-align="after">
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>from</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>to</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>nights</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>action</fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-header>

                                            <fo:table-body>

                                                <xsl:for-each select="terms/minimumStays/rule">

                                                    <fo:table-row>

                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                            <fo:block><xsl:value-of select="@start"/></fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                                <fo:block><xsl:value-of select="@end"/></fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                                <fo:block><xsl:value-of select="@nights"/></fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                                <fo:block><xsl:value-of select="@descriptionforpdf"/></fo:block>
                                                        </fo:table-cell>
                                                    </fo:table-row>

                                                </xsl:for-each>

                                            </fo:table-body>
                                        </fo:table>
                                    </xsl:when>
                                    <xsl:otherwise><fo:block font-size="10pt" margin-top="1mm" margin-left="1mm">Do not apply.</fo:block></xsl:otherwise>
                                </xsl:choose>


                            </fo:block>
                        <!-- 6. Días entrada -->
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
                                                <fo:block font-weight="700" font-size="12pt">9. Mandatory checkin and checkout days <!-- <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Dias de entrada y salida obligatorios</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>

                                <xsl:choose>
                                    <xsl:when test="terms/weekDays/rule">

                                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                            <fo:table-column column-width="20mm"></fo:table-column>
                                            <fo:table-column column-width="20mm"></fo:table-column>
                                            <fo:table-column column-width="40mm"></fo:table-column>
                                            <fo:table-column column-width="40mm"></fo:table-column>
                                            <fo:table-column column-width="40mm"></fo:table-column>

                                            <fo:table-header>
                                                <fo:table-row display-align="after">
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>from</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>to</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>week days</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>on</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>result when not accomplished</fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-header>

                                            <fo:table-body>

                                                <fo:table-row>

                                                    <fo:table-cell
                                                            text-align="right"
                                                            font-size="10pt"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="0.3px">
                                                        <xsl:for-each select="terms/weekDays/rule">
                                                            <fo:block><xsl:value-of select="@start"/></fo:block>
                                                        </xsl:for-each>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            text-align="right"
                                                            font-size="10pt"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="0.3px">
                                                        <xsl:for-each select="terms/weekDays/rule">
                                                            <fo:block><xsl:value-of select="@end"/></fo:block>
                                                        </xsl:for-each>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            text-align="right"
                                                            font-size="10pt"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="0.3px">
                                                        <xsl:for-each select="terms/weekDays/rule">
                                                            <fo:block><xsl:value-of select="@weekDaysString"/></fo:block>
                                                        </xsl:for-each>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            text-align="right"
                                                            font-size="10pt"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="0.3px">
                                                        <xsl:for-each select="terms/weekDays/rule">
                                                            <fo:block>
                                                                <xsl:if test="@checkin">checkin</xsl:if>
                                                                <xsl:if test="@checkout">, checkout</xsl:if>
                                                                <xsl:if test="@stay">, stay</xsl:if>
                                                            </fo:block>
                                                        </xsl:for-each>
                                                    </fo:table-cell>
                                                    <fo:table-cell
                                                            text-align="right"
                                                            font-size="10pt"
                                                            padding="1mm"
                                                            border-right-style="solid"
                                                            border-right-width="0.3px">
                                                        <xsl:for-each select="terms/weekDays/rule">
                                                            <fo:block><xsl:choose><xsl:when test="@onRequest">leave on request</xsl:when><xsl:otherwise>not allow</xsl:otherwise></xsl:choose></fo:block>
                                                        </xsl:for-each>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-body>
                                        </fo:table>

                                    </xsl:when>
                                    <xsl:otherwise><fo:block font-size="10pt" margin-top="1mm" margin-left="1mm">Do not apply.</fo:block></xsl:otherwise>
                                </xsl:choose>

                            </fo:block>

                        <!-- 7. Ofertas varias -->

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
                                                <fo:block font-weight="700" font-size="12pt">10. Offers <!-- <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Ofertas</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>

                                <xsl:choose>
                                    <xsl:when test="offers/offer">

                                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                            <fo:table-column column-width="174mm"></fo:table-column>

                                            <fo:table-header>
                                                <fo:table-row display-align="after">
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>description</fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-header>

                                            <fo:table-body>
                                                <xsl:for-each select="offers/offer">
                                                    <fo:table-row>
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                            <fo:block><xsl:value-of select="@description"/></fo:block><fo:block><xsl:value-of select="@formal"/></fo:block>
                                                        </fo:table-cell>
                                                    </fo:table-row>
                                                </xsl:for-each>
                                            </fo:table-body>
                                        </fo:table>

                                    </xsl:when>
                                    <xsl:otherwise><fo:block font-size="10pt" margin-top="1mm" margin-left="1mm">Do not apply.</fo:block></xsl:otherwise>
                                </xsl:choose>

                            </fo:block>

                        <!-- 8. Condiciones de cancelación -->
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
                                                <fo:block font-weight="700" font-size="12pt">11. Cancellation terms <!-- <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Condiciones de cancelación</fo:inline> --></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </fo:table-body>
                                </fo:table>


                                <xsl:choose>
                                    <xsl:when test="cancellation/line">

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
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>from</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>to</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>release</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>amount</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>percent</fo:block>
                                                    </fo:table-cell>
                                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold" border-after-style="solid" border-after-width="0.3px">
                                                        <fo:block>first nights</fo:block>
                                                    </fo:table-cell>
                                                </fo:table-row>
                                            </fo:table-header>

                                            <fo:table-body>
                                                <xsl:for-each select="cancellation/line">
                                                    <fo:table-row>
                                                        <fo:table-cell>
                                                            <fo:block></fo:block>
                                                        </fo:table-cell>

                                                        <fo:table-cell>
                                                            <fo:block></fo:block>
                                                        </fo:table-cell>

                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                            <fo:block><xsl:value-of select="@start"/></fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                            <fo:block><xsl:value-of select="@end"/></fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                            <fo:block><xsl:value-of select="@release"/></fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                            <fo:block><xsl:value-of select="@amount"/></fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                            <fo:block><xsl:value-of select="@percent"/></fo:block>
                                                        </fo:table-cell>
                                                        <fo:table-cell
                                                                text-align="right"
                                                                font-size="10pt"
                                                                padding="1mm"
                                                                border-right-style="solid"
                                                                border-right-width="0.3px">
                                                            <fo:block><xsl:value-of select="@firstNights"/></fo:block>
                                                        </fo:table-cell>
                                                    </fo:table-row>
                                                </xsl:for-each>
                                            </fo:table-body>
                                        </fo:table>

                                    </xsl:when>
                                    <xsl:otherwise><fo:block font-size="10pt" margin-top="1mm" margin-left="1mm">Do not apply.</fo:block></xsl:otherwise>
                                </xsl:choose>


                            </fo:block>






                        <!-- 12. Pagos -->

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
                                            <fo:block font-weight="700" font-size="12pt">12. Payments<!--  <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Release</fo:inline> --></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                </fo:table-body>
                            </fo:table>


                            <xsl:choose>
                                <xsl:when test="payment/line">

                                    <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                        <fo:table-column column-width="40mm"></fo:table-column>
                                        <fo:table-column column-width="20mm"></fo:table-column>
                                        <fo:table-column column-width="20mm"></fo:table-column>

                                        <fo:table-header>
                                            <fo:table-row>
                                                <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold">
                                                    <fo:block>ref. date</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold">
                                                    <fo:block>release</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold">
                                                    <fo:block>percent</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-header>

                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="10pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px"
                                                        border-before-style="solid"
                                                        border-before-width="0.3px">
                                                    <xsl:for-each select="payment/line">
                                                        <fo:block><xsl:value-of select="@referenceDate"/></fo:block>
                                                    </xsl:for-each>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="10pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px"
                                                        border-before-style="solid"
                                                        border-before-width="0.3px">
                                                    <xsl:for-each select="payment/line">
                                                        <fo:block><xsl:value-of select="@release"/></fo:block>
                                                    </xsl:for-each>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="10pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px"
                                                        border-before-style="solid"
                                                        border-before-width="0.3px">
                                                    <xsl:for-each select="payment/line">
                                                        <fo:block><xsl:value-of select="@percent"/> %</fo:block>
                                                    </xsl:for-each>
                                                </fo:table-cell>                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>


                                </xsl:when>

                                <xsl:otherwise>
                                    <fo:block font-size="10pt" margin-top="1mm" margin-left="1mm">Do not apply.</fo:block>
                                </xsl:otherwise>
                            </xsl:choose>


                        </fo:block>


                        <!-- 12. Vencimientos -->

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
                                            <fo:block font-weight="700" font-size="12pt">13. Due dates<!--  <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Release</fo:inline> --></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                </fo:table-body>
                            </fo:table>


                            <xsl:choose>
                                <xsl:when test="dueDates/dueDate">

                                    <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" border-collapse="separate">

                                        <fo:table-column column-width="20mm"></fo:table-column>
                                        <fo:table-column column-width="40mm"></fo:table-column>

                                        <fo:table-header>
                                            <fo:table-row>
                                                <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold">
                                                    <fo:block>date</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell text-align="right" font-size="10pt" padding="1mm" font-weight="bold">
                                                    <fo:block>amount</fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-header>

                                        <fo:table-body>
                                            <fo:table-row>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="10pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px"
                                                        border-before-style="solid"
                                                        border-before-width="0.3px">
                                                    <xsl:for-each select="dueDates/dueDate">
                                                        <fo:block><xsl:value-of select="@date"/></fo:block>
                                                    </xsl:for-each>
                                                    <fo:block>TOTAL</fo:block>
                                                </fo:table-cell>
                                                <fo:table-cell
                                                        text-align="right"
                                                        font-size="10pt"
                                                        padding="1mm"
                                                        border-right-style="solid"
                                                        border-right-width="0.3px"
                                                        border-before-style="solid"
                                                        border-before-width="0.3px">
                                                    <xsl:for-each select="dueDates/dueDate">
                                                        <fo:block><xsl:value-of select="@amount"/></fo:block>
                                                    </xsl:for-each>
                                                    <fo:block font-weight="700"><xsl:value-of select="dueDates/@total"/></fo:block>
                                                </fo:table-cell>
                                            </fo:table-row>
                                        </fo:table-body>
                                    </fo:table>


                                </xsl:when>

                                <xsl:otherwise>
                                    <fo:block font-size="10pt" margin-top="1mm" margin-left="1mm">Do not apply.</fo:block>
                                </xsl:otherwise>
                            </xsl:choose>


                        </fo:block>





                        <!-- 13. Special terms -->

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
                                            <fo:block font-weight="700" font-size="12pt">14. Special terms <!-- <fo:inline  font-weight="100" font-size="10pt" font-style="italic">Condiciones de cancelación</fo:inline> --></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                </fo:table-body>
                            </fo:table>


                            <xsl:choose>
                                <xsl:when test="specialTerms">
                                    <xsl:for-each select="specialTerms/line">
                                        <fo:block>
                                            <xsl:value-of select="."/>
                                        </fo:block>
                                    </xsl:for-each>
                                </xsl:when>
                                <xsl:otherwise>
                                    <fo:block font-size="10pt" margin-top="1mm" margin-left="1mm">Do not apply.</fo:block>
                                </xsl:otherwise>
                            </xsl:choose>


                        </fo:block>



                    </fo:flow>
                </fo:page-sequence>

                <fo:page-sequence master-reference="last">

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
                                                   font-size="10pt">
                                        <xsl:for-each select="office/company">
                                            <fo:block><fo:external-graphic src="{@logo}" content-height="scale-to-fit" height="13.3mm" content-width="scale-to-fit" width="31.2mm"/></fo:block>
                                            <fo:block font-weight="bold">
                                                <xsl:value-of select="@name"/>
                                            </fo:block>
                                            <fo:block>
                                                <xsl:value-of select="@businessName"/>
                                            </fo:block>
                                            <fo:block>
                                                <xsl:value-of select="@address"/>
                                            </fo:block>
                                            <fo:block>
                                                <xsl:value-of select="@vatIdentificationNumber"/>
                                            </fo:block>
                                            <fo:block>
                                                t. <xsl:value-of select="@telephone"/>
                                            </fo:block>
                                            <fo:block>
                                                <xsl:value-of select="@email"/>
                                            </fo:block>
                                        </xsl:for-each>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="9pt" font-weight="bold" padding="1mm">
                                        <fo:block >
                                            <xsl:value-of select="@title"></xsl:value-of>
                                        </fo:block>
                                        <fo:block margin-top="2mm">
                                            <xsl:value-of select="@type"/>
                                        </fo:block>
                                        <xsl:if test="warranty">
                                            <fo:block margin-top="2mm">
                                                WARRANTY
                                            </fo:block>
                                        </xsl:if>
                                        <fo:block margin-top="2mm">
                                            <xsl:value-of select="@currencyCode"/>
                                        </fo:block>
                                        <fo:block margin-top="2mm">VAT
                                            <xsl:value-of select="@vat"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="10pt" padding="1mm">
                                        <fo:block font-weight="bold">
                                            <xsl:value-of select="hotel/@name"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="hotel/@category"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="hotel/@address"/>
                                        </fo:block>
                                        <fo:block>
                                            t. <xsl:value-of select="hotel/@telephone"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="hotel/@fax"/>
                                        </fo:block>
                                        <fo:block>
                                            <xsl:value-of select="hotel/@email"/>
                                        </fo:block>
                                        <!--                                        <fo:block font-weight="bold"><xsl:value-of select="objeto/@nombre"/> <xsl:value-of select="objeto/@categoria"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@nombrefiscal"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@cif"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@direccion"/></fo:block>                                        <xsl:if test="objeto/@poblacion"><fo:block><xsl:value-of select="objeto/@poblacion"/></fo:block></xsl:if>                                        <xsl:if test="objeto/@provincia"><fo:block><xsl:value-of select="objeto/@provincia"/></fo:block></xsl:if>                                        <xsl:if test="objeto/@pais"><fo:block><xsl:value-of select="objeto/@pais"/></fo:block></xsl:if>                                        <fo:block>t:<xsl:value-of select="objeto/@telefono"/></fo:block>                                        <fo:block>f:<xsl:value-of select="objeto/@fax"/></fo:block>                                        <fo:block><xsl:value-of select="objeto/@email"/></fo:block>                                        -->
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
                                    <fo:table-cell font-size="10pt" padding="1mm"
                                                   display-align="after">
                                        <fo:block>And as proof of consent and acceptance of all the clauses , including
                                            those contained in page <fo:page-number-citation-last ref-id="end"/>, the two
                                            sides signed in <xsl:value-of select="@signedAt"/>,
                                            <xsl:value-of select="@signatureDate"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="10pt" font-weight="bold" padding="1mm"
                                                   display-align="after">
                                        <fo:block>Name and surname</fo:block>
                                        <fo:block>By
                                            <xsl:value-of select="@partnerSignatory"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell text-align="right" font-size="10pt" font-weight="bold" padding="1mm"
                                                   display-align="after">
                                        <fo:block>Name and surname</fo:block>
                                        <fo:block>By
                                            <xsl:value-of select="@ownSignatory"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>
                        <fo:block text-align="right" font-size="10pt" font-family="Liberation Sans Narrow">Page <fo:page-number></fo:page-number> of
                            <fo:page-number-citation-last ref-id="end"/>
                        </fo:block>
                    </fo:static-content>

                    <!-- Content -->

                    <fo:flow flow-name="xsl-region-body">

                        <!-- Cláusulas del contrato -->
                        <fo:block font-size="7.7pt" font-family="Liberation Sans Narrow" font-weight="bold" space-before="5mm" space-after="1mm">
                            Contract clauses
                        </fo:block>

                        <xsl:if test="clauses/clause">

                            <fo:list-block font-size="7.7pt" font-family="Liberation Sans Narrow" provisional-distance-between-starts="0.3cm" provisional-label-separation="0.15cm">

                                <xsl:for-each select="clauses/clause">

                                    <fo:list-item  padding="1mm">
                                        <fo:list-item-label end-indent="label-end()">
                                            <fo:block>
                                                <xsl:value-of select="position()"></xsl:value-of>
                                            </fo:block>
                                        </fo:list-item-label>
                                        <fo:list-item-body start-indent="body-start()">
                                            <fo:block>
                                                <xsl:value-of select="@text"/>
                                            </fo:block>
                                        </fo:list-item-body>
                                    </fo:list-item>
                                </xsl:for-each>

                            </fo:list-block>

                        </xsl:if>

                         <!-- Delegaciones -->
                        <fo:block padding-top="3mm" border-top-style="solid" border-top-width="1px" font-size="7.7pt" font-family="Liberation Sans Narrow" font-weight="bold" space-before="5mm" space-after="1mm">Offices</fo:block>

                        <xsl:if test="offices/office">

                            <fo:table>
                                <fo:table-body font-size="7.7pt" font-family="Liberation Sans Narrow" space-before="5mm">

                                    <xsl:for-each select="offices/office">

                                        <fo:table-row>
                                            <fo:table-cell padding-before="4mm">
                                                <fo:block page-break-inside="avoid">
                                                    <fo:block font-weight="bold"><xsl:value-of select="@name"/></fo:block>
                                                    <fo:block>Tel <xsl:value-of select="@tel"/></fo:block>
                                                    <xsl:if test="@fax"><fo:block>Fax <xsl:value-of select="@fax"/></fo:block></xsl:if>
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
