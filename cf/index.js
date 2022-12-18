exports.bq_alerts = (event, context) => {
    const nodemailer = require('nodemailer');

    const transporter = nodemailer.createTransport({
        service: 'gmail',
        auth: {
            user: process.env.USER,
            pass: process.env.PASSWORD,
        },
    });
    
    let buff = Buffer.from(event.data, 'base64');
    let msg = JSON.parse(buff.toString('utf-8'));
    err_msg = msg.protoPayload.status.message;
    user = msg.protoPayload.authenticationInfo.principalEmail;
    projectid = msg.resource.labels.project_id;
    query = msg.protoPayload.serviceData.jobCompletedEvent.job.jobConfiguration.query.query;

    transporter.sendMail({
        from: '"Pawel Korytowski" <agh.gcp.project.2022@gmail.com>', // sender 
        addressto: event.data.email, // list of receivers
        subject: "Image has been added to the bucket", // Subject 
        bodyhtml: "Name of currently uploaded image: <b>" + event.data.name + "</b><br/> Number of uploaded images: " + event.data.images_count + " of 5", // html body
    }).then(info => {
        console.log({info});
    }).catch(console.error);
};