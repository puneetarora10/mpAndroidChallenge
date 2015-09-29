# mpAndroidChallenge

Mixpanel Android coding challenge
You are the WWD’s newest mobile developer! The World-Wide Directory (WWD) is a new initiative to
catalogue basic information about everyone in the world. Your first project is to build a directory of
everyone in their database as an Android app. As it turns out, the WWD has decided to store each
record in Mixpanel as people profiles. People records are JSON objects that look like:
{
"$distinct_id":"9196",
"$properties":{
"$city":"Houston",
"$country_code":"US",
"$created":"20150804T10:
08:13",
"$email":"john.doe@gmail.com",
"$name":"John Doe",
"$region":"Texas",
"phone":"2025551239",
"photo_url":"https://s3.amazonaws.com/images.mxpnl.com/images/profile_pic_1.gif"
}
}

Each people record may have, but is not required to have the properties in the sample above. You
can get the data by making a signed HTTP request to Mixpanel. We have provided a basic function
that will return a JSONArray response. Use the response to populate a mobile application that
looks like the following:

Feel free to use whatever technology best showcases your skill, but do not use third-party libraries
-- we know you can use libraries, we want to see you write code! Build it with the expectation that
you will add to it and maintain it for the foreseeable future.
You can take up to 90 minutes. Please be mindful of architecture with a focus on maintainability
and extensibility over styling. Good luck!
