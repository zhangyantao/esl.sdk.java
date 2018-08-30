package com.silanis.esl.sdk.examples;


import com.silanis.esl.sdk.*;
import com.silanis.esl.sdk.builder.PackageBuilder;
import com.silanis.esl.sdk.io.Files;
import org.junit.Test;

import static com.silanis.esl.sdk.builder.DocumentBuilder.newDocumentWithName;
import static com.silanis.esl.sdk.builder.SignatureBuilder.signatureFor;
import static com.silanis.esl.sdk.builder.SignerBuilder.newSignerWithEmail;

// 提供给策略适配平台的接口样例
public class ESignTest {

    public static final String API_KEY = "";
    public static final String API_URL = "https://sandbox.esignlive.com/api";
    // USE https://apps.esignlive.com/api FOR PRODUCTION

    public String signRecordID = "";

    // 发起签署
    @Test
    public void signPdf() {

        EslClient eslClient = new EslClient( API_KEY, API_URL );

        DocumentPackage documentPackage = PackageBuilder.newPackageNamed( "Test Package Java SDK" )
                .withSigner( newSignerWithEmail( "signers.email@example.com" )
                        .withCustomId( "Signer" )
                        .withFirstName( "SignerFirstName" )
                        .withLastName( "SignerLastName" ) )
                .withSigner( newSignerWithEmail( "your.email@example.com" )
                        .withFirstName( "YourFirstName" )
                        .withLastName( "YourLastName" ) )
                .withDocument( newDocumentWithName( "sampleAgreement" )
                        .fromFile( "your file path" )
                        .withSignature( signatureFor( "signers.email@example.com" )
                                .onPage( 0 )
                                .atPosition( 175, 165 ) )
                        .withSignature( signatureFor( "your.email@example.com")
                                .onPage( 0 )
                                .atPosition( 550, 165 )))
                .build();

        // Issue the request to the eSignLive server to create the DocumentPackage
        PackageId packageId = eslClient.createPackageOneStep( documentPackage );

        // Send the package to be signed by the participants
        eslClient.sendPackage( packageId );

        // 测试使用packageID作为签署记录ID，或者策略适配平台自己分配。
        signRecordID = packageId.getId();

        return;

    }

    @Test
    public void querySignStatus() {

        EslClient eslClient = new EslClient( API_KEY, API_URL );

        //create a packageId using you packageId string
        PackageId packageId = new PackageId(signRecordID);
        //get your package
        DocumentPackage sentPackage = eslClient.getPackage(packageId);

        // 文档状态
        PackageStatus status = sentPackage.getStatus();
        // 几种状态
        // ARCHIVED, COMPLETED, DECLINED, DRAFT, EXPIRED, OPTED_OUT, and SENT.

        // 签署状态
        SigningStatus sentSigningStatus = eslClient.getSigningStatus( packageId, null, null );
        System.out.println(sentSigningStatus.getToken());

        // 把eSignLive 的状态转换成策略适配平台的状态。

    }

    @Test
    public void getSignedFile() {

        EslClient eslClient = new EslClient( API_KEY, API_URL );

        //create a packageId using you packageId string
        PackageId packageId = new PackageId(signRecordID);

        // 下载签署结果文件
        byte[] documentZip = eslClient.downloadZippedDocuments(packageId);
        Files.saveTo(documentZip, "documentZip.zip");
        System.out.println("Document Zip File Downloaded");

        // 下载证据报告
        //download evidence summary
        byte[] evidenceSummary = eslClient.downloadEvidenceSummary(packageId);
        Files.saveTo(evidenceSummary, "evidenceSummary.pdf");
        System.out.println("Evidence Summary Downloaded");



    }


}
