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
            <xsl:for-each select="//all">
                <fo:page-sequence master-reference="dinA4">

                    <!-- Header -->
                    <fo:static-content flow-name="xsl-region-before">
                        <fo:block>WORLD</fo:block>
                    </fo:static-content>

                    <!-- Footer -->
                    <fo:static-content flow-name="xsl-region-after">
                        <fo:block text-align="right" font-size="8pt" font-family="Liberation Sans Narrow">Page <fo:page-number></fo:page-number> of
                            <fo:page-number-citation-last ref-id="end"/>
                        </fo:block>
                    </fo:static-content>

                    <!-- Content -->
                    <fo:flow flow-name="xsl-region-body">

                        <fo:block>

                            <xsl:if test="country">
                        <fo:list-block>
                                <xsl:for-each select="country">
                                    <fo:list-item>
                                    <fo:list-item-label end-indent="label-end()">
                                        <fo:block>
                                            <fo:inline>·</fo:inline>
                                        </fo:block>
                                    </fo:list-item-label>
                                    <fo:list-item-body start-indent="body-start()">
                                    <fo:block><xsl:value-of select="@name"></xsl:value-of></fo:block>
                                        <xsl:if test="state">
                                            <fo:list-block>
                                                <xsl:for-each select="state">
                                                    <fo:list-item>
                                                        <fo:list-item-label end-indent="label-end()">
                                                            <fo:block>
                                                                <fo:inline>·</fo:inline>
                                                            </fo:block>
                                                        </fo:list-item-label>
                                                        <fo:list-item-body start-indent="body-start()">
                                                            <fo:block><xsl:value-of select="@name"></xsl:value-of></fo:block>
                                                            <xsl:if test="city">
                                                                <fo:list-block>
                                                                    <xsl:for-each select="city">
                                                                        <fo:list-item>
                                                                            <fo:list-item-label end-indent="label-end()">
                                                                                <fo:block>
                                                                                    <fo:inline>·</fo:inline>
                                                                                </fo:block>
                                                                            </fo:list-item-label>
                                                                            <fo:list-item-body start-indent="body-start()">
                                                                                <fo:block><xsl:value-of select="@name"></xsl:value-of></fo:block>
                                                                                <xsl:if test="transferpoint">
                                                                                    <fo:list-block>
                                                                                        <xsl:for-each select="transferpoint">
                                                                                            <fo:list-item>
                                                                                                <fo:list-item-label end-indent="label-end()">
                                                                                                    <fo:block>
                                                                                                        <fo:inline>·</fo:inline>
                                                                                                    </fo:block>
                                                                                                </fo:list-item-label>
                                                                                                <fo:list-item-body start-indent="body-start()">
                                                                                                    <fo:block><xsl:value-of select="@name"></xsl:value-of></fo:block>




                                                                                                </fo:list-item-body>
                                                                                            </fo:list-item>
                                                                                        </xsl:for-each>
                                                                                    </fo:list-block>
                                                                                </xsl:if>
                                                                            </fo:list-item-body>
                                                                        </fo:list-item>
                                                                    </xsl:for-each>
                                                                </fo:list-block>
                                                            </xsl:if>
                                                        </fo:list-item-body>
                                                    </fo:list-item>
                                                </xsl:for-each>
                                            </fo:list-block>
                                        </xsl:if>
                                    </fo:list-item-body>
                                    </fo:list-item>
                                </xsl:for-each>
                        </fo:list-block>
                            </xsl:if>

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