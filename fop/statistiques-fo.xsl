<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" 
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:fo="http://www.w3.org/1999/XSL/Format">
  
  <xsl:template match="/">
    <fo:root>
      <!-- Configuration de la page -->
      <fo:layout-master-set>
        <fo:simple-page-master master-name="A4" 
          page-height="29.7cm" 
          page-width="21cm"
          margin-top="2cm"
          margin-bottom="2cm"
          margin-left="2cm"
          margin-right="2cm">
          <fo:region-body/>
        </fo:simple-page-master>
      </fo:layout-master-set>
      
      <!-- Contenu du document -->
      <fo:page-sequence master-reference="A4">
        <fo:flow flow-name="xsl-region-body">
          
          <!-- Titre principal -->
          <fo:block 
            font-size="28pt" 
            font-weight="bold" 
            text-align="center"
            space-after="0.3cm"
            color="#667eea">
            Classement des Équipes
          </fo:block>
          
          <!-- Sous-titre -->
          <fo:block 
            font-size="14pt" 
            text-align="center"
            space-after="1.5cm"
            color="#764ba2">
            Statistiques complètes de la saison
          </fo:block>
          
          <!-- Tableau -->
          <fo:table table-layout="fixed" width="100%">
            <!-- Colonnes avec proportional-column-width -->
            <fo:table-column column-width="proportional-column-width(4)"/>
            <fo:table-column column-width="proportional-column-width(2)"/>
            <fo:table-column column-width="proportional-column-width(2)"/>
            <fo:table-column column-width="proportional-column-width(2)"/>
            
            <!-- En-tête du tableau -->
            <fo:table-header background-color="#f8f9fa">
              <fo:table-row>
                <fo:table-cell padding="10pt" border-bottom="3pt solid #667eea">
                  <fo:block font-weight="bold" color="#333" text-align="center">ÉQUIPE</fo:block>
                </fo:table-cell>
                <fo:table-cell padding="10pt" border-bottom="3pt solid #667eea">
                  <fo:block font-weight="bold" color="#333" text-align="center">VICTOIRES</fo:block>
                </fo:table-cell>
                <fo:table-cell padding="10pt" border-bottom="3pt solid #667eea">
                  <fo:block font-weight="bold" color="#333" text-align="center">DÉFAITES</fo:block>
                </fo:table-cell>
                <fo:table-cell padding="10pt" border-bottom="3pt solid #667eea">
                  <fo:block font-weight="bold" color="#333" text-align="center">CLASSEMENT</fo:block>
                </fo:table-cell>
              </fo:table-row>
            </fo:table-header>
            
            <!-- Corps du tableau -->
            <fo:table-body>
              <xsl:for-each select="statistiques_equipe/equipe">
                <xsl:sort select="classement" data-type="number" order="ascending"/>
                <xsl:variable name="pos" select="position()"/>
                
                <fo:table-row>
                  <!-- Coloration selon le classement -->
                  <xsl:choose>
                    <xsl:when test="$pos &lt;= 3">
                      <xsl:attribute name="background-color">#d4edda</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$pos &gt; 12">
                      <xsl:attribute name="background-color">#f8d7da</xsl:attribute>
                    </xsl:when>
                    <xsl:when test="$pos mod 2 = 0">
                      <xsl:attribute name="background-color">#ffffff</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                      <xsl:attribute name="background-color">#f9f9f9</xsl:attribute>
                    </xsl:otherwise>
                  </xsl:choose>
                  
                  <!-- Équipe -->
                  <fo:table-cell padding="10pt" border-bottom="1pt solid #eee">
                    <fo:block text-align="center" color="#555">
                      <xsl:if test="$pos &lt;= 3 or $pos &gt; 12">
                        <xsl:attribute name="font-weight">bold</xsl:attribute>
                      </xsl:if>
                      <xsl:value-of select="@nom"/>
                    </fo:block>
                  </fo:table-cell>
                  
                  <!-- Victoires -->
                  <fo:table-cell padding="10pt" border-bottom="1pt solid #eee">
                    <fo:block text-align="center" color="#555">
                      <xsl:if test="$pos &lt;= 3 or $pos &gt; 12">
                        <xsl:attribute name="font-weight">bold</xsl:attribute>
                      </xsl:if>
                      <xsl:value-of select="victoires"/>
                    </fo:block>
                  </fo:table-cell>
                  
                  <!-- Défaites -->
                  <fo:table-cell padding="10pt" border-bottom="1pt solid #eee">
                    <fo:block text-align="center" color="#555">
                      <xsl:if test="$pos &lt;= 3 or $pos &gt; 12">
                        <xsl:attribute name="font-weight">bold</xsl:attribute>
                      </xsl:if>
                      <xsl:value-of select="defaites"/>
                    </fo:block>
                  </fo:table-cell>
                  
                  <!-- Classement -->
                  <fo:table-cell padding="10pt" border-bottom="1pt solid #eee">
                    <fo:block text-align="center" color="#555" font-weight="bold">
                      <xsl:value-of select="classement"/>
                    </fo:block>
                  </fo:table-cell>
                </fo:table-row>
              </xsl:for-each>
            </fo:table-body>
          </fo:table>
          
        </fo:flow>
      </fo:page-sequence>
    </fo:root>
  </xsl:template>
  
</xsl:stylesheet>
