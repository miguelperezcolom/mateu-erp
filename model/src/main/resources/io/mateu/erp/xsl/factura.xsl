<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:fo="http://www.w3.org/1999/XSL/Format"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output indent="yes"/>
    <xsl:template match="/">
        <xsl:variable name="moneda"><xsl:value-of select="@moneda"/></xsl:variable>
        <xsl:variable name="monedacontable"><xsl:value-of select="@monedacontable"/></xsl:variable>
        <xsl:variable name="exchangerate"><xsl:value-of select="@exchangerate"/></xsl:variable>



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
                        <fo:table-column column-width="54mm"></fo:table-column>
                        <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.2px"></fo:table-column>
                        <fo:table-column column-width="60mm" border-right-style="solid" border-right-width="0.2px"></fo:table-column>

                        <fo:table-body>
                            <fo:table-row>
                                <fo:table-cell text-align="left"  font-size="8pt">
                                    <fo:block><fo:external-graphic src="url('{@urllogo}')" content-width="scale-to-fit" width="40mm"/></fo:block>
                                    <fo:block><xsl:value-of select="issuer/@businessName"/> - NIF: <xsl:value-of select="issuer/@vatid"/></fo:block>
                                    <fo:block><xsl:value-of select="issuer/@address"/> - <xsl:value-of select="issuer/@zip"/> <xsl:value-of select="issuer/@resort"/></fo:block>
                                    <fo:block><xsl:value-of select="issuer/@email"/> - t. <xsl:value-of select="issuer/@telephone"/> - f. <xsl:value-of select="issuer/@fax"/></fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right" font-weight="bold" padding="1mm">
                                    <fo:block font-size="9pt" space-after="-2pt">FACTURA Nº <xsl:value-of select="@number"/></fo:block>
                                    <fo:block font-size="7pt" font-style="italic" space-after="2pt">Invoice number</fo:block>
                                    <fo:block font-size="9pt" space-after="-2pt">FECHA DE EMISIÓN <xsl:value-of select="@date"/></fo:block>
                                    <fo:block font-size="7pt" font-style="italic" space-after="2pt">Emission date</fo:block>
                                    <fo:block font-size="9pt" space-after="-2pt">FECHA DE VENCIMIENTO <xsl:value-of select="@duedate"/></fo:block>
                                    <fo:block font-size="7pt" font-style="italic" space-after="2pt">Due date </fo:block>
                                </fo:table-cell>
                                <fo:table-cell text-align="right" font-size="8pt" padding="1mm">
                                    <fo:block font-weight="bold"><xsl:value-of select="recipient/@name"/></fo:block>
                                    <fo:block><xsl:value-of select="recipient/@businessName"/></fo:block>
                                    <fo:block>NIF: <xsl:value-of select="recipient/@vatid"/></fo:block>
                                    <fo:block><xsl:value-of select="recipient/@address"/></fo:block>
                                    <fo:block><xsl:value-of select="recipient/@zip"/> <xsl:value-of select="recipient/@resort"/></fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-body>

                    </fo:table>
                </fo:static-content>

                <!-- Footer -->
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block font-family="Liberation Sans Narrow" text-align="center" font-size="6pt" padding="1mm">Inscrita en el Reg. Mercantil de Baleares, Folio 53 del Tomo 656 del Archivo, Libro 571 Sección 3a de Sociedades, Hoja no 14.746, Incrip. 1a C.I.F. A07234636.</fo:block>
                    <fo:block font-family="Liberation Sans Narrow" text-align="center" font-size="6pt" padding="1mm">En cumplimiento de la Ley de Servicios de la Sociedad de la Información y de Comercio Electrónico y de la Vigente ley Orgánica 15/1999 de Protección de Datos Española, le comunicamos que su dirección postal forma parte de nuestra base de datos con la finalidad de informar y gestionar reservas. Así mismo le notificamos la posibilidad de ejercer sus derechos de acceso, rectificación, cancelación u oposición de esos datos dirigiéndose a Viajes Es Freus, s.l., Carrer de Navarra no 7, 07800 Eivissa (Illes Balears). Esta factura va dirigida, de manera exclusiva, a su destinatario y contiene información confidencial y sujeta al secreto profesional, cuya divulgación no está permitida por la ley. En caso de haber recibido esta factura por error, le rogamos que, de forma inmediata, nos lo comunique y proceda a su eliminación. Asimismo, le comunicamos que la distribución, copia o utilización de esta factura, cualquiera que fuera su finalidad, están prohibidos por la ley.</fo:block>

                    <fo:block text-align="right" font-size="8pt" font-family="Liberation Sans Narrow">Página <fo:page-number></fo:page-number> de <fo:page-number-citation-last ref-id="end"/></fo:block>

                </fo:static-content>

                <!-- Content -->
                <fo:flow flow-name="xsl-region-body" >


                    <xsl:for-each select="//invoice">

                        <!--
                        <fo:block>FACTURA</fo:block>
                        -->


                        <!-- TÍTULO -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow">
                            <fo:table-column></fo:table-column>
                            <fo:table-body>
                                <fo:table-row>
                                    <fo:table-cell border-bottom-style="solid" border-right-width="1px" padding="1mm">
                                        <xsl:choose>
                                            <xsl:when test="@number = 'PROFORMA'">
                                                <fo:block font-weight="700" font-size="12pt">PROFORMA <fo:inline font-size="9pt" font-style="italic" font-weight="normal">PRO-FORMA</fo:inline></fo:block>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <fo:block font-weight="700" font-size="12pt">FACTURA <fo:inline font-size="9pt" font-style="italic" font-weight="normal">INVOICE</fo:inline></fo:block>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>
                        </fo:table>

                        <!-- COLUMNAS -->
                        <fo:table table-layout="fixed" width="174mm" font-family="Liberation Sans Narrow" font-size="8pt" border-collapse="collapse">

                            <fo:table-column column-width="15mm"></fo:table-column>
                            <fo:table-column column-width="20mm"></fo:table-column>
                            <fo:table-column column-width="25mm"></fo:table-column>
                            <fo:table-column column-width="40mm"></fo:table-column>
                            <fo:table-column column-width="54mm"></fo:table-column>
                            <fo:table-column column-width="20mm"></fo:table-column>

                            <!-- CONCEPTOS -->

                            <xsl:for-each select="lines">

                                <fo:table-body>
                                    <fo:table-row>
                                        <fo:table-cell padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row>
                                        <fo:table-cell number-columns-spanned="6" padding="1mm">
                                            <fo:block font-weight="bold"><xsl:value-of select="@titulo"/> <fo:inline font-style="italic" font-weight="normal"><xsl:value-of select="@texto"/></fo:inline></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>localizador</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>referencia</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>fecha</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>cliente / servicio</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>concepto</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>total</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:for-each select="line">

                                        <fo:table-row>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@id"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@reference"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@start"/>-<xsl:value-of select="@end"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@leadName"/> / <xsl:value-of select="@service"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@subject"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@total"/> €</fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>


<!--
                                    <fo:table-row>
                                        <fo:table-cell
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="0.2px">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="0.2px">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="0.2px">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="0.2px">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                text-align="right"
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="0.2px">
                                            <fo:block text-align="right">Total servicio:</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                text-align="right"
                                                padding="1mm"
                                                border-right-style="solid"
                                                border-right-width="0.2px">
                                            <fo:block text-align="right"><xsl:value-of select="@total"/> €</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
-->
                                </fo:table-body>
                            </xsl:for-each>

                            <!-- PRECIO FINAL -->
                            <!-- PRECIO FINAL -->
                            <fo:table-body font-weight="bold">
                                <fo:table-row>
                                    <fo:table-cell padding="3mm">
                                        <fo:block></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell
                                            number-columns-spanned="4"
                                            text-align="right"
                                            padding="1mm">
                                        <fo:block text-align="right"><xsl:if test="@watermark"><fo:external-graphic src="url('{@watermark}')" content-width="scale-to-fit" width="30mm"/></xsl:if></fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell
                                            number-columns-spanned="1"
                                            text-align="right"
                                            padding="1mm"
                                            border-right-style="solid"
                                            border-right-width="0.2px">

                                        <!--

                                         <impuesto tipo="VAT@GENERAL" porcentaje="15,00" total="38,35" base="255,65" vat="VAT" regime="GENERAL" />

                                         -->


                                        <xsl:for-each select="impuesto">
                                            <fo:block space-after="3pt">

                                                <xsl:choose>
                                                    <xsl:when test="@regime = 'SPECIAL'">Total IVA incluido (régimen especial)</xsl:when>
                                                    <xsl:when test="vat = ''">Total IVA no aplicable</xsl:when>
                                                    <xsl:otherwise>Base with <xsl:value-of select="@porcentaje"/>% IVA</xsl:otherwise>
                                                </xsl:choose>

                                                <xsl:if test="$moneda != $monedacontable">(<xsl:value-of select="@accountingbase"/>&#x00A0;<xsl:value-of select="@accountingcurrency"/>)</xsl:if>
                                            </fo:block>

                                            <xsl:if test="@total != '0,00'">
                                                <fo:block space-after="3pt"><xsl:value-of select="@vat"/> (<xsl:value-of select="@porcentaje"/>%) <xsl:if test="$moneda != $monedacontable">(<xsl:value-of select="@accountingvalue"/>&#x00A0;<xsl:value-of select="@accountingcurrency"/>)</xsl:if></fo:block>
                                            </xsl:if>

                                        </xsl:for-each>

                                        <fo:block space-after="3pt" color="white">x</fo:block>

                                        <xsl:if test="@pvp">
                                            <fo:block space-after="3pt">PVP <xsl:if test="$moneda != $monedacontable">(<xsl:value-of select="@pvpaccounting"/>&#x00A0;<xsl:value-of select="$monedacontable"/>)</xsl:if></fo:block>
                                        </xsl:if>
                                        <xsl:if test="@neto">
                                            <fo:block space-after="3pt">neto a pagar<xsl:if test="$moneda != $monedacontable">(<xsl:value-of select="@netoaccounting"/>&#x00A0;<xsl:value-of select="$monedacontable"/>)</xsl:if></fo:block>
                                        </xsl:if>
                                        <fo:block space-after="3pt">pagado <xsl:if test="$moneda != $monedacontable">(<xsl:value-of select="@pagadoaccounting"/>&#x00A0;<xsl:value-of select="$monedacontable"/>)</xsl:if></fo:block>
                                        <fo:block space-after="3pt">pendiente de pago <xsl:if test="$moneda != $monedacontable">(<xsl:value-of select="@pendienteaccounting"/>&#x00A0;<xsl:value-of select="$monedacontable"/>)</xsl:if></fo:block>

                                        <xsl:if test="$moneda != $monedacontable"><fo:block space-after="3pt" color="white">x</fo:block><fo:block space-after="3pt">Exchage rate: <xsl:value-of select="$exchangerate"/></fo:block></xsl:if>

                                    </fo:table-cell>
                                    <fo:table-cell
                                            text-align="right"
                                            padding="1mm"
                                            border-right-style="solid"
                                            border-right-width="0.2px">
                                        <xsl:for-each select="impuesto">

                                            <fo:block space-after="3pt"><xsl:value-of select="@base"/>&#x00A0;<xsl:value-of select="$moneda"/></fo:block>

                                            <xsl:if test="@total != '0,00'">
                                                <fo:block space-after="3pt"><xsl:value-of select="@total"/>&#x00A0;<xsl:value-of select="$moneda"/></fo:block>
                                            </xsl:if>
                                        </xsl:for-each>

                                        <fo:block space-after="3pt" color="white">x</fo:block>

                                        <xsl:if test="@pvp">
                                            <fo:block space-after="3pt"><xsl:value-of select="@pvp"/>&#x00A0;<xsl:value-of select="$moneda"/></fo:block>
                                        </xsl:if>
                                        <xsl:if test="@neto">
                                            <fo:block space-after="3pt"><xsl:value-of select="@neto"/>&#x00A0;<xsl:value-of select="$moneda"/></fo:block>
                                        </xsl:if>
                                        <fo:block space-after="3pt"><xsl:value-of select="@paid"/>&#x00A0;<xsl:value-of select="$moneda"/> €</fo:block>
                                        <fo:block space-after="3pt"><xsl:value-of select="@pending"/>&#x00A0;<xsl:value-of select="$moneda"/> €</fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </fo:table-body>

                            <!-- CANCELACIÓN -->


                            <fo:table-body>

                                <xsl:if test="reserva">
                                    <fo:table-row>
                                        <fo:table-cell padding="5mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>
                                    <fo:table-row>
                                        <fo:table-cell number-columns-spanned="6" padding="1mm">
                                            <fo:block>GASTOS DE CANCELACIÓN</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <fo:table-row>
                                        <fo:table-cell number-columns-spanned="4" padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>concepto</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>desde</fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell text-align="right" padding="1mm" font-weight="bold" border-bottom-style="solid" border-bottom-width="0.2px">
                                            <fo:block>valoración</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                    <xsl:for-each select="reserva/servicio/costecancelacion">

                                        <fo:table-row>
                                            <fo:table-cell
                                                    number-columns-spanned="4"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="../@descripcion"/></fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@desde"/> 00:00 AM</fo:block>
                                            </fo:table-cell>
                                            <fo:table-cell
                                                    text-align="right"
                                                    padding="1mm"
                                                    border-right-style="solid"
                                                    border-right-width="0.2px">
                                                <fo:block><xsl:value-of select="@coste"/></fo:block>
                                            </fo:table-cell>
                                        </fo:table-row>
                                    </xsl:for-each>
                                    <fo:table-row>
                                        <fo:table-cell
                                                number-columns-spanned="4"
                                                padding="1mm">
                                            <fo:block></fo:block>
                                        </fo:table-cell>
                                        <fo:table-cell
                                                font-size="6pt"
                                                padding="1mm">
                                            <fo:block>* Fechas y horas de horario local del país de destino.</fo:block>
                                        </fo:table-cell>
                                    </fo:table-row>

                                </xsl:if>

                                <fo:table-row>
                                    <fo:table-cell padding="5mm">
                                        <fo:block></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                                <fo:table-row>
                                    <fo:table-cell number-columns-spanned="6" padding="1mm">
                                        <fo:block><xsl:value-of select="facturadopor/@pago"/></fo:block>
                                    </fo:table-cell>
                                </fo:table-row>


                            </fo:table-body>


                        </fo:table>



                    </xsl:for-each>

                    <fo:block id="end"></fo:block>

                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>