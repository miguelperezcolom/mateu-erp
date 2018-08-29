<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output indent="yes"/>
    <xsl:template match="/factura">
        <fo:root>
            <fo:layout-master-set>

                <!-- A4 size -->
                <fo:simple-page-master master-name="dinA4" page-height="29.7cm" page-width="21cm" margin="2mm 20mm 35mm 20mm">
                    <fo:region-body margin-top="31mm"/>
                    <fo:region-before display-align="after"/>
                    <fo:region-after display-align="after"/>
                </fo:simple-page-master>
            </fo:layout-master-set>
            <fo:page-sequence master-reference="dinA4">

                <!-- Header -->
                <fo:static-content flow-name="xsl-region-before">

                    <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                        <fo:table-column column-width="174mm"></fo:table-column>

                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left"  font-size="8pt">
                                    <fo:block><xsl:value-of select="facturadopor/@nombrefiscal"/> - NIF: <xsl:value-of select="facturadopor/@cif"/></fo:block>
                                    <fo:block><xsl:value-of select="facturadopor/@direccion"/> - <xsl:value-of select="facturadopor/@ncp"/> <xsl:value-of select="facturadopor/@ciudad"/></fo:block>
                                    <fo:block><xsl:value-of select="facturadopor/@email"/> - t. <xsl:value-of select="facturadopor/@telefono"/> - f. <xsl:value-of select="facturadopor/@fax"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right" font-weight="bold" padding="1mm">
                                    <fo:block font-size="9pt" space-after="-2pt"></fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right" font-size="8pt" padding="1mm">
                                    <fo:block font-weight="bold"><xsl:value-of select="destinatario/@nombre"/></fo:block>
                                    <fo:block><xsl:value-of select="destinatario/@nombrefiscal"/></fo:block>
                                    <fo:block>NIF: <xsl:value-of select="destinatario/@cif"/></fo:block>
                                    <fo:block><xsl:value-of select="destinatario/@direccion"/></fo:block>
                                    <fo:block><xsl:value-of select="destinatario/@cp"/> <xsl:value-of select="destinatario/@ciudad"/></fo:block>
                                    <fo:block>-</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>

                    </fo:table>
                </fo:static-content>

                <!-- Footer -->
                <fo:static-content flow-name="xsl-region-after">

                    <xsl:if test="facturadopor/@pago"><fo:block font-family="Liberation Sans Narrow"  font-size="9pt" space-after="20pt" padding="1mm">INSTRUCCIONES PAGO: <xsl:value-of select="facturadopor/@pago"/></fo:block></xsl:if>

                    <fo:block font-family="Liberation Sans Narrow" text-align="center" font-size="6pt" padding="1mm">Inscrita en el Reg. Mercantil de Baleares, Folio 53 del Tomo 656 del Archivo, Libro 571 Sección 3a de Sociedades, Hoja no 14.746, Incrip. 1a C.I.F. A07234636.</fo:block>
                    <fo:block font-family="Liberation Sans Narrow" text-align="center" font-size="6pt" padding="1mm">En cumplimiento de la Ley de Servicios de la Sociedad de la Información y de Comercio Electrónico y de la Vigente ley Orgánica 15/1999 de Protección de Datos Española, le comunicamos que su dirección postal forma parte de nuestra base de datos con la finalidad de informar y gestionar reservas. Así mismo le notificamos la posibilidad de ejercer sus derechos de acceso, rectificación, cancelación u oposición de esos datos dirigiéndose a Viajes Es Freus, s.l., Carrer de Navarra no 7, 07800 Eivissa (Illes Balears). Esta factura va dirigida, de manera exclusiva, a su destinatario y contiene información confidencial y sujeta al secreto profesional, cuya divulgación no está permitida por la ley. En caso de haber recibido esta factura por error, le rogamos que, de forma inmediata, nos lo comunique y proceda a su eliminación. Asimismo, le comunicamos que la distribución, copia o utilización de esta factura, cualquiera que fuera su finalidad, están prohibidos por la ley.</fo:block>

                    <fo:block text-align="right" font-size="8pt" font-family="Liberation Sans Narrow">Página <fo:page-number></fo:page-number> de <fo:page-number-citation-last ref-id="end"/></fo:block>

                </fo:static-content>

                <!-- Content -->
                <fo:flow flow-name="xsl-region-body" >

                    <xsl:for-each select="coletilla">
                        <fo:block><xsl:value-of select="."/></fo:block>
                    </xsl:for-each>

                    <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" font-size="8pt" border-collapse="collapse">



                        <fo:table-column column-width="20mm"></fo:table-column>
                        <fo:table-column column-width="20mm"></fo:table-column>
                        <fo:table-column column-width="20mm"></fo:table-column>
                        <fo:table-column column-width="20mm"></fo:table-column>
                        <fo:table-column column-width="20mm"></fo:table-column>
                        <fo:table-column column-width="20mm"></fo:table-column>
                        <fo:table-column column-width="20mm"></fo:table-column>

                        <fo:table-header>
                            <fo:table-row>
                                <fo:table-cell padding="2mm">
                                    <fo:block></fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                            <fo:table-row>
                                <fo:table-cell padding="1mm" font-weight="bold" color="white" background-color="black">
                                    <fo:block>Fecha</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="1mm" font-weight="bold" color="white" background-color="black">
                                    <fo:block>Factura</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="1mm" font-weight="bold" color="white" background-color="black">
                                    <fo:block>Reserva</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="1mm" font-weight="bold" color="white" background-color="black">
                                    <fo:block>Vto.</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="1mm" font-weight="bold" color="white" background-color="black">
                                    <fo:block>Total factura</fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right" padding="1mm" font-weight="bold" color="white" background-color="black">
                                    <fo:block>Pagado</fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right" padding="1mm" font-weight="bold" color="white" background-color="black">
                                    <fo:block>Pendiente</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>

                        <fo:table-body>

                            <xsl:for-each select="factura">

                                <fo:table-row>
                                    <fo:table-cell padding="1mm">
                                        <fo:block><xsl:value-of select="@fecha"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="1mm">
                                        <fo:block><xsl:value-of select="@numero"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="1mm">
                                        <fo:block><xsl:value-of select="@localizadores"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="1mm">
                                        <fo:block><xsl:value-of select="@vencimiento"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="1mm" text-align="right">
                                        <fo:block>T<xsl:value-of select="@total"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="1mm" text-align="right">
                                        <fo:block><xsl:value-of select="@pagado"/></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell padding="1mm" text-align="right">
                                        <fo:block><xsl:value-of select="@pendiente"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>


                            </xsl:for-each>

                            <fo:table-row>
                                <fo:table-cell padding="1mm">
                                    <fo:block></fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="1mm">
                                    <fo:block></fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="1mm">
                                    <fo:block></fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="1mm" font-weight="bold">
                                    <fo:block>Totales</fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="1mm" text-align="right" font-weight="bold">
                                    <fo:block>T<xsl:value-of select="@total"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="1mm" text-align="right" font-weight="bold">
                                    <fo:block><xsl:value-of select="@pagado"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell padding="1mm" text-align="right" font-weight="bold">
                                    <fo:block><xsl:value-of select="@pendiente"/></fo:block>
                                </fo:table-cell>
                            </fo:table-row>


                        </fo:table-body>


                    </fo:table>

                    <fo:block id="end"/>

                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
    <xsl:template match="*"/>
</xsl:stylesheet>
