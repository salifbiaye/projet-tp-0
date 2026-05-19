<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
  <xsl:template match="/">
    <html>
      <head>
        <meta charset="UTF-8"/>
        <title>Classement des Équipes Sportives</title>
        <style>
          * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
          }
          
          body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
          }
          
          .container {
            background: white;
            border-radius: 12px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            overflow: hidden;
            max-width: 800px;
            width: 100%;
          }
          
          .header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 40px 20px;
            text-align: center;
            color: white;
          }
          
          .header h1 {
            font-size: 2.5em;
            font-weight: 700;
            letter-spacing: 0.5px;
            margin-bottom: 5px;
          }
          
          .header p {
            font-size: 1em;
            opacity: 0.9;
            font-weight: 300;
          }
          
          .table-wrapper {
            overflow-x: auto;
            padding: 30px;
          }
          
          table {
            width: 100%;
            border-collapse: collapse;
          }
          
          thead {
            background-color: #f8f9fa;
          }
          
          th {
            padding: 15px 20px;
            text-align: center;
            font-weight: 700;
            color: #333;
            font-size: 0.95em;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            border-bottom: 3px solid #667eea;
          }
          
          td {
            padding: 15px 20px;
            text-align: center;
            color: #555;
            font-size: 0.95em;
            border-bottom: 1px solid #eee;
          }
          
          tbody tr {
            transition: all 0.3s ease;
          }
          
          tbody tr:hover {
            transform: scale(1.02);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }
          
          .top-three {
            background-color: #d4edda;
            font-weight: 600;
            color: #155724;
          }
          
          .top-three:hover {
            background-color: #c3e6cb;
          }
          
          .bottom-three {
            background-color: #f8d7da;
            font-weight: 600;
            color: #721c24;
          }
          
          .bottom-three:hover {
            background-color: #f5c6cb;
          }
          
          tbody tr:nth-child(odd):not(.top-three):not(.bottom-three) {
            background-color: #f9f9f9;
          }
          
          tbody tr:nth-child(even):not(.top-three):not(.bottom-three) {
            background-color: #ffffff;
          }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="header">
            <h1>🏆 Classement des Équipes</h1>
            <p>Statistiques complètes de la saison</p>
          </div>
          
          <div class="table-wrapper">
            <table>
              <thead>
                <tr>
                  <th>Équipe</th>
                  <th>Victoires</th>
                  <th>Défaites</th>
                  <th>Classement</th>
                </tr>
              </thead>
              <tbody>
                <xsl:for-each select="statistiques_equipe/equipe">
                  <xsl:sort select="classement" data-type="number" order="ascending"/>
                  <xsl:variable name="pos" select="position()"/>
                  <tr>
                    <xsl:choose>
                      <xsl:when test="$pos &lt;= 3">
                        <xsl:attribute name="class">top-three</xsl:attribute>
                      </xsl:when>
                      <xsl:when test="$pos &gt; 12">
                        <xsl:attribute name="class">bottom-three</xsl:attribute>
                      </xsl:when>
                    </xsl:choose>
                    <td><xsl:value-of select="@nom"/></td>
                    <td><xsl:value-of select="victoires"/></td>
                    <td><xsl:value-of select="defaites"/></td>
                    <td><strong><xsl:value-of select="classement"/></strong></td>
                  </tr>
                </xsl:for-each>
              </tbody>
            </table>
          </div>
        </div>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>