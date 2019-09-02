function downloadResource(inputUrl, format, fileName, parsingType) {
  $.ajax({
      url: inputUrl,
      type: "GET",
      headers: {
          "Accept": format
      },
      success: function(data) {
          var parsedData;
          if (parsingType == 1)
              parsedData = data;
          if (parsingType == 2)
              parsedData = xmlToString(data);
          if (parsingType == 3)
              parsedData = JSON.stringify(data);

          var element = document.createElement('a');
          element.setAttribute('href', 'data:' + format + ',' + encodeURIComponent(parsedData));
          element.setAttribute('download', fileName);

          element.style.display = 'none';
          document.body.appendChild(element);

          element.click();

          document.body.removeChild(element);
      },
      error: function(jqxhr, textStatus, errorThrown) {
          console.log(textStatus, errorThrown)
      }
  });
}

function xmlToString(xmlData) {

    var xmlString;
    //IE
    if (window.ActiveXObject) {
        xmlString = xmlData.xml;
    }
    // code for Mozilla, Firefox, Opera, etc.
    else {
        xmlString = (new XMLSerializer()).serializeToString(xmlData);
    }
    return xmlString;
}