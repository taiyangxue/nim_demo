package com.netease.nim.demo.home.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.netease.nim.demo.R;
import com.netease.nim.demo.common.entity.bmob.Update;
import com.netease.nim.demo.common.util.SharedPreferencesUtils;
import com.netease.nim.demo.login.LoginActivity;
import com.netease.nim.demo.main.activity.MyMainActivity;

import java.io.File;



public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";
    private String pkName;
    private String versionName;
    @ViewInject(R.id.tv_version)
    private TextView tv_version;
    @ViewInject(R.id.progressBar)
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        ViewUtils.inject(this);
        try {
            pkName = this.getPackageName();
            versionName = this.getPackageManager().getPackageInfo(
                    pkName, 0).versionName;
            Log.e(TAG, pkName + versionName);
            tv_version.setText("v"+versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        showMsgDialog();
//        enterHome();
    }

    protected void showUpdateDialog(final Update update) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("版本更新");
        builder.setMessage(update.getDesc());
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("更新升级", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    //下载文件
//                    BmobFile file = update.getFile();
//                    final File saveFile = new File(Environment.getExternalStorageDirectory(), file.getFilename());
//                    file.download(saveFile, new DownloadFileListener() {
//
//                        @Override
//                        public void onStart() {
////                            toast("开始下载...");
//                            progressBar.setVisibility(View.VISIBLE);
//                        }
//
//                        @Override
//                        public void done(String savePath, BmobException e) {
//                            if (e == null) {
//                                Log.e(TAG, "下载成功,保存路径:" + savePath);
////                                下载成功,保存路径:/data/data/com.sst.admin/cache/bmob/app-debug.apk
//                                install(saveFile);
//                                finish();
//                            } else {
//                                Log.e(TAG, "下载失败：" + e.getErrorCode() + "," + e.getMessage());
//                            }
//                        }
//
//                        @Override
//                        public void onProgress(Integer value, long newworkSpeed) {
////                            Log.i("bmob","下载进度："+value+","+newworkSpeed);
//                            progressBar.setMax(100);
//                            progressBar.setProgress(value);
//                        }
//                    });
                } else {
                    Toast.makeText(SplashActivity.this, "未检测到sd卡", Toast.LENGTH_SHORT);
                }
            }
        });
        builder.setNegativeButton("忽略本次", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
                dialog.dismiss();
            }
        });
        builder.show();
    }
    protected void showMsgDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("隐私政策与用户协议");
        builder.setMessage("隐私政策与用户协议\n" +
                "壹度教育重视用户的隐私。您在使用我们的服务时，我们可能会收集和使用您的相关信息。我们希望通过本《隐私政策》向您说明，在使用我们的服务时，我们如何收集、使用、储存和分享这些信息，以及我们为您提供的访问、更新、控制和保护这些信息的方式。本《隐私政策》与您所使用的服务息息相关，希望您仔细阅读，在需要时，按照本《隐私政策》的指引，作出您认为适当的选择。本《隐私政策》中涉及的相关技术词汇，我们尽量以简明扼要的表述，并提供进一步说明的链接，以便您的理解。\n" +
                "您使用或继续使用我们的服务，即意味着同意我们按照本《隐私政策》收集、使用、储存和分享您的相关信息。\n" +
                "如对本《隐私政策》或相关事宜有任何问题，请通过1545557983@qq.com与我们联系。\n" +
                "我们可能收集的信息\n" +
                "我们提供服务时，可能会收集、储存和使用下列与您有关的信息。如果您不提供相关信息，可能无法享受我们提供的某些服务，或者无法达到相关服务拟达到的效果。\n" +
                "您提供的信息\n" +
                "您在使用我们的服务时，向我们提供的相关个人信息，例如电话号码、电子邮件或银行卡号等；\n" +
                "您通过我们的服务向其他方提供的共享信息，以及您使用我们的服务时所储存的信息。\n" +
                "其他方分享的您的信息\n" +
                "其他方使用我们的服务时所提供有关您的共享信息。\n" +
                "我们获取的您的信息\n" +
                "您使用服务时我们可能收集如下信息：\n" +
                "日志信息，指您使用我们的服务时，系统可能通过cookies、web beacon或其他方式自动采集的技术信息，包括：\n" +
                "设备或软件信息，例如您的移动设备、网页浏览器或用于接入我们服务的其他程序所提供的配置信息、您的IP地址和移动设备所用的版本和设备识别码；\n" +
                "在使用我们服务时搜索或浏览的信息，例如您使用的网页搜索词语、访问的社交媒体页面url地址，以及您在使用我们服务时浏览或要求提供的其他信息和内容详情；\n" +
                "有关您曾使用的移动应用（APP）和其他软件的信息，以及您曾经使用该等移动应用和软件的信息；\n" +
                "位置信息，指您开启设备定位功能并使用我们基于位置提供的相关服务时，收集的有关您位置的信息，包括：\n" +
                "您通过具有定位功能的移动设备使用我们的服务时，通过GPS或WiFi等方式收集的您的地理位置信息；\n" +
                "您或其他用户提供的包含您所处地理位置的实时信息，例如您提供的账户信息中包含的您所在地区信息，您或其他人上传的显示您当前或曾经所处地理位置的共享信息；\n" +
                "您可以通过关闭定位功能，停止对您的地理位置信息的收集。\n" +
                "我们可能如何使用信息\n" +
                "我们可能将在向您提供服务的过程之中所收集的信息用作下列用途：\n" +
                "向您提供服务；\n" +
                "在我们提供服务时，用于身份验证、客户服务、安全防范、诈骗监测、存档和备份用途，确保我们向您提供的产品和服务的安全性；\n" +
                "帮助我们设计新服务，改善我们现有服务；\n" +
                "使我们更加了解您如何接入和使用我们的服务，从而针对性地回应您的个性化需求，例如语言设定、位置设定、个性化的帮助服务和指示，或对您和其他用户作出其他方面的回应；\n" +
                "向您提供与您更加相关的广告以替代普遍投放的广告；\n" +
                "评估我们服务中的广告和其他促销及推广活动的效果，并加以改善；\n" +
                "软件认证或管理软件升级；\n" +
                "让您参与有关我们产品和服务的调查。\n" +
                "为了让您有更好的体验、改善我们的服务或您同意的其他用途，在符合相关法律法规的前提下，我们可能将通过某一项服务所收集的信息，以汇集信息或者个性化的方式，用于我们的其他服务。例如，在您使用我们的一项服务时所收集的信息，可能在另一服务中用于向您提供特定内容，或向您展示与您相关的、非普遍推送的信息。如果我们在相关服务中提供了相应选项，您也可以授权我们将该服务所提供和储存的信息用于我们的其他服务。\n" +
                "您如何访问和控制自己的个人信息\n" +
                "我们将尽一切可能采取适当的技术手段，保证您可以访问、更新和更正自己的注册信息或使用我们的服务时提供的其他个人信息。在访问、更新、更正和删除前述信息时，我们可能会要求您进行身份验证，以保障账户安全。\n" +
                "我们可能分享的信息\n" +
                "除以下情形外，未经您同意，我们不会与任何第三方分享您的个人信息：\n" +
                "我们可能将您的个人信息与第三方服务供应商、承包商及代理（例如代表我们发出电子邮件或推送通知的通讯服务提供商、为我们提供位置数据的地图服务供应商）分享（他们可能并非位于您所在的法域），用作下列用途：\n" +
                "向您提供我们的服务；\n" +
                "实现“我们可能如何使用信息”部分所述目的；\n" +
                "履行我们在本《隐私政策》中的义务和行使我们的权利；\n" +
                "理解、维护和改善我们的服务。\n" +
                "如我们与任何上述第三方分享您的个人信息，我们将努力确保该等第三方在使用您的个人信息时遵守本《隐私政策》及我们要求其遵守的其他适当的保密和安全措施。\n" +
                "随着我们业务的持续发展，我们有可能进行合并、收购、资产转让或类似的交易，您的个人信息有可能作为此类交易的一部分而被转移。我们将在转移前通知您。\n" +
                "我们还可能为以下需要而保留、保存或披露您的个人信息：\n" +
                "遵守适用的法律法规；\n" +
                "遵守法院命令或其他法律程序的规定；\n" +
                "遵守相关政府机关的要求；\n" +
                "为遵守适用的法律法规、维护社会公共利益，或保护我们的客户、我们、其他用户的人身和财产安全或合法权益所合理必需的用途。\n" +
                "信息安全\n" +
                "我们仅在本《隐私政策》所述目的所必需的期间和法律法规要求的时限内保留您的个人信息。 我们使用各种安全技术和程序，以防信息的丢失、不当使用、未经授权阅览或披露。例如，在某些服务中，我们将利用加密技术（例如SSL）来保护您提供的个人信息。但请您理解，由于技术的限制以及可能存在的各种恶意手段，在互联网行业，即便竭尽所能加强安全措施，也不可能始终保证信息百分之百的安全。您需要了解，您接入我们的服务所用的系统和通讯网络，有可能因我们可控范围外的因素而出现问题。\n" +
                "您分享的信息\n" +
                "我们的多项服务，可让您不仅与自己的社交网络，也与使用该服务的所有用户公开分享您的相关信息，例如，您在我们的服务中所上传或发布的信息（包括您公开的个人信息、您建立的名单）、您对其他人上传或发布的信息作出的回应，以及包括与这些信息有关的位置数据和日志信息。使用我们服务的其他用户也有可能分享与您有关的信息（包括位置数据和日志信息）。特别是，我们的社交媒体服务，是专为使您与世界各地的用户共享信息而设计，您可以使共享信息实时、广泛地传递。只要您不删除共享信息，有关信息会一直留存在公共领域；即使您删除共享信息，有关信息仍可能由其他用户或不受我们控制的非关联第三方独立地缓存、复制或储存，或由其他用户或该等第三方在公共领域保存。\n" +
                "因此，请您谨慎考虑通过我们的服务上传、发布和交流的信息内容。在一些情况下，您可通过我们某些服务的隐私设定来控制有权浏览您共享信息的用户范围。如要求从我们的服务中删除您的相关信息，请通过该等特别服务条款提供的方式操作。\n" +
                "您分享的敏感个人信息\n" +
                "某些个人信息因其特殊性可能被认为是敏感个人信息，例如您的种族、宗教、个人健康和医疗信息等。相比其他个人信息，敏感个人信息受到更加严格的保护。\n" +
                "请注意，您在使用我们的服务时所提供、上传或发布的内容和信息（例如有关您社交活动的照片等信息），可能会泄露您的敏感个人信息。您需要谨慎地考虑，是否在使用我们的服务时披露相关敏感个人信息。\n" +
                "您同意按本《隐私政策》所述的目的和方式来处理您的敏感个人信息。\n" +
                "我们可能如何收集信息\n" +
                "我们或我们的第三方合作伙伴，可能通过cookies和web beacon收集和使用您的信息，并将该等信息储存为日志信息。\n" +
                "我们使用自己的cookies和web beacon，目的是为您提供更个性化的用户体验和服务，并用于以下用途：\n" +
                "记住您的身份。例如：cookies和web beacon有助于我们辨认您作为我们的注册用户的身份，或保存您向我们提供的有关您的喜好或其他信息；\n" +
                "分析您使用我们服务的情况。例如，我们可利用cookies和web beacon来了解您使用我们的服务进行什么活动，或哪些网页或服务最受您的欢迎；\n" +
                "广告优化。Cookies和web beacon有助于我们根据您的信息，向您提供与您相关的广告而非进行普遍的广告投放。\n" +
                "我们为上述目的使用cookies和web beacon的同时，可能将通过cookies和web beacon收集的非个人身份信息，经统计加工后提供给广告商或其他合作伙伴，用于分析用户如何使用我们的服务，并用于广告服务。\n" +
                "我们的产品和服务上可能会有广告商或其他合作方放置的cookies和web beacon。这些cookies和web beacon可能会收集与您相关的非个人身份信息，以用于分析用户如何使用该等服务、向您发送您可能感兴趣的广告，或用于评估广告服务的效果。这些第三方cookies和web beacon收集和使用该等信息，不受本《隐私政策》约束，而是受相关使用者的隐私政策约束，我们不对第三方的cookies或web beacon承担责任。\n" +
                "您可以通过浏览器设置拒绝或管理cookies或web beacon。但请注意，如果停用cookies或web beacon，您有可能无法享受最佳的服务体验，某些服务也可能无法正常使用。同时，您还会收到同样数量的广告，但这些广告与您的相关性会降低。\n" +
                "广告服务\n" +
                "我们可能使用您的相关信息，向您提供与您更加相关的广告。\n" +
                "我们也可能使用您的信息，通过我们的服务、电子邮件或其他方式向您发送营销信息，提供或推广我们或第三方的如下商品和服务：\n" +
                "我们的服务，我们的关联公司和合作伙伴的商品或服务，包括即时通讯服务、网上媒体服务、互动娱乐服务、社交网络服务、付款服务、互联网搜索服务、位置和地图服务、应用软件和服务、数据管理软件和服务、网上广告服务、互联网金融，以及其他社交媒体、娱乐、电子商务、资讯和通讯软件或服务（统称“互联网服务”）；\n" +
                "第三方互联网服务供应商，以及与下列有关的第三方商品或服务：食物和餐饮、体育、音乐、电影、电视、现场表演及其他艺术和娱乐、书册、杂志和其他刊物、服装和配饰、珠宝、化妆品、个人健康和卫生、电子、收藏品、家用器皿、电器、家居装饰和摆设、宠物、汽车、酒店、交通和旅游、银行、保险及其他金融服务、会员积分和奖励计划，以及我们认为可能与您相关的其他商品或服务。\n" +
                "如您不希望我们将您的个人信息用作前述广告用途，您可以通过我们在广告中提供的相关提示，或在特定服务中提供的指引，要求我们停止为上述用途使用您的个人信息。\n" +
                "我们可能向您发送的邮件和信息\n" +
                "邮件和信息推送\n" +
                "您在使用我们的服务时，我们可能使用您的信息向您的设备发送电子邮件、新闻或推送通知。如您不希望收到这些信息，可以按照我们的相关提示，在设备上选择取消订阅。\n" +
                "与服务有关的公告\n" +
                "我们可能在必要时（例如因系统维护而暂停某一项服务时）向您发出与服务有关的公告。您可能无法取消这些与服务有关、性质不属于推广的公告。\n" +
                "隐私政策的适用例外\n" +
                "我们的服务可能包括或链接至第三方提供的社交媒体或其他服务（包括网站）。例如：\n" +
                "您利用 “分享”键将某些内容分享到我们的服务，或您利用第三方连线服务登录我们的服务。这些功能可能会收集您的相关信息（包括您的日志信息），并可能在您的电脑装置cookies，从而正常运行上述功能；\n" +
                "我们通过广告或我们服务的其他方式向您提供链接，使您可以接入第三方的服务或网站。\n" +
                "该等第三方社交媒体或其他服务可能由相关的第三方或我们运营。您使用该等第三方的社交媒体服务或其他服务（包括您向该等第三方提供的任何个人信息），须受该第三方的服务条款及隐私政策（而非《通用服务条款》或本《隐私政策》）约束，您需要仔细阅读其条款。本《隐私政策》仅适用于我们所收集的信息，并不适用于任何第三方提供的服务或第三方的信息使用规则，我们对任何第三方使用由您提供的信息不承担任何责任。\n" +
                "未成年人使用我们的服务\n" +
                "我们鼓励父母或监护人指导未满十八岁的未成年人使用我们的服务。我们建议未成年人鼓励他们的父母或监护人阅读本《隐私政策》，并建议未成年人在提交的个人信息之前寻求父母或监护人的同意和指导。\n" +
                "隐私政策的适用范围\n" +
                "除某些特定服务外，我们所有的服务均适用本《隐私政策》。这些特定服务将适用特定的隐私政策。针对某些特定服务的特定隐私政策，将更具体地说明我们在该等服务中如何使用您的信息。该特定服务的隐私政策构成本《隐私政策》的一部分。如相关特定服务的隐私政策与本《隐私政策》有不一致之处，适用该特定服务的隐私政策。\n" +
                "请您注意，本《隐私政策》不适用于以下情况：\n" +
                "通过我们的服务而接入的第三方服务（包括任何第三方网站）收集的信息；\n" +
                "通过在我们服务中进行广告服务的其他公司或机构所收集的信息。\n" +
                "变更\n" +
                "我们可能适时修订本《隐私政策》的条款，该等修订构成本《隐私政策》的一部分。如该等修订造成您在本《隐私政策》下权利的实质减少，我们将在修订生效前通过在主页上显著位置提示或向您发送电子邮件或以其他方式通知您。在该种情况下，若您继续使用我们的服务，即表示同意受经修订的本《隐私政策》的约束。\n" +
                "用户协议\n" +
                "（以下简称“本公司”）按照下列条款与条件提供信息和产品，您在本协议中亦可被称为“用户”，以下所述条款和条件将构成您与本公司，就您使用提供的内容所达成的全部协议（以下称“本协议”）。\n" +
                "说明\n" +
                "本公司向您提供包括但不限于游戏下载、使用、充值、客户服务、游戏资讯、论坛交流等服务（以下称“本服务”）。本公司针对本服务所制定的相关规定，包括但不限于本公司在游戏平台下运营的任何一款游戏所包含的游戏规则、用户处罚条例，客服条例等，以及本公司就账号使用及管理、游戏充值等服务制定的相关服务协议、规则。本公司在此提示用户，请您在使用本服务前详细阅读本协议的所有内容，尤其是免除、限制本公司责任或者限制用户权利的条款（特别是粗体下划线标注的内容），如您对本协议有任何疑问，请向本公司（0755-29303544）进行咨询。一旦您使用本服务，即表示您已阅读并完全同意接受本协议项下所述条款和条件的约束。如果您不同意本协议的任何条款，请您不要使用本服务。未成年人应经其监护人陪同阅读本服务协议并表示同意，方可接受本服务协议。监护人应加强对未成年人的监督和保护，因其未谨慎履行监护责任而损害未成年人利益或者本公司利益的，应由监护人承担责任。\n" +
                "权利声明\n" +
                "1、本公司及其关联公司（关联企业指与本公司存在直接或间接，股权或以协议安排等其他形式的控制与被控制关系，以及对公司运营具有重大影响关系的公司法人）享有并保留以下各项内容完整的、不可分割的所有权及/或知识产权：\n" +
                "（1）游戏平台相关的软件、技术、程序、代码、用户界面等；\n" +
                "（2）本服务相关的商标、图形标记。\n" +
                "2、本公司提供的服务内容中所涉及的游戏，文字、软件、声音、图片、动画、录像、图表等，均受相关知识产权法以及其他相关法律的保护。未经本公司或者其他相关权利人授权，用户不得复制、使用、修改、摘编、翻译、发行,第三方未经本公司及/或其相关权利人的书面许可，不得以任何方式擅自进行使用。\n" +
                "责任限制\n" +
                "1、本公司向用户提供的服务均是在依\"现状\"提供，本公司在此明确声明对本服务不作任何明示或暗示的保证，包括但不限于对服务的可适用性、准确性、及时性、可持续性等。\n" +
                "2、用户理解并同意自行承担使用本服务的风险，且用户在使用本服务时，应遵循中国法律的相关规定，由于用户行为所造成的任何损害和后果，本公司均不承担除法律有明确规定外的责任。\n" +
                "3、不论在何种情况下，本公司均不对由于网络连接故障、通讯线路、第三方网站、电脑硬件等任何原因给用户造成的任何损失承担除法律有明确规定外的责任。\n" +
                "用户行为规范\n" +
                "1、用户在本网站注册时，不得使用虚假身份信息。用户应当妥善保存其账户信息和密码，由于用户泄密所导致的损失需由用户自行承担。如用户发现他人冒用或盗用其账户或密码，或其账户存在其他未经合法授权使用之情形，应立即以有效方式通知本公司。用户理解并同意本公司有权根据用户的通知、请求或依据判断，采取相应的行动或措施，包括但不限于冻结账户、限制账户功能等，本公司对采取上述行动所导致的损失不承担除法律有明确规定外的责任。\n" +
                "2、用户在使用本服务时须遵守法律法规，不得利用本服务从事违法违规行为，包括但不限于：\n" +
                "（1）发布、传送、传播、储存危害国家安全统一、破坏社会稳定、违反公序良俗、侮辱、诽谤、淫秽、暴力以及任何违反国家法律法规的内容；\n" +
                "（2）发布、传送、传播、储存侵害他人知识产权、商业秘密等合法权利的内容；\n" +
                "（3）恶意虚构事实、隐瞒真相以误导、欺骗他人；\n" +
                "（4）发布、传送、传播广告信息及垃圾信息；\n" +
                "（5）其他法律法规禁止的行为。\n" +
                "3、用户不得利用本服务进行任何有损本公司及其关联企业之权利、利益及商誉，或其他用户合法权利之行为。\n" +
                "4、用户不得基于本服务从事制作、使用、传播“私服”、“外挂”等侵害本公司合法权益的行为。如有违反，本公司将依据中国现行法律法规及本公司的相关规定予以处理。\n" +
                "5、虚拟财产转移服务外，用户不得通过任何方式直接或变相进行游戏账号、游戏币、游戏道具等虚拟财产的转移。\n" +
                "6、用户不得从事作弊等损害游戏公平性的行为。用户承诺接受本公司对其游戏数据进行分析，当本公司发现其数据异常时，本公司可根据自己的独立判断认定其为作弊。\n" +
                "7、用户不得从事任何利用本公司平台系统漏洞进行有损其他用户、本公司或互联网安全的行为。\n" +
                "8、用户知悉并确认，本公司通过公告、邮件、短信、账户通知以及用户在账户中登记的即时通讯工具等方式，向用户发出关于本服务的通知、规则、提示等信息，均为有效通知。该等信息一经公布或发布，即视为已送达至用户。\n" +
                "广告信息和促销\n" +
                "1、用户同意接受本公司通过公告、邮件、短信、账户通知以及用户在账户中登记的即时通讯工具等方式发送的有关本服务，或本公司、本公司之关联企业或与本公司有合作关系的第三方相关的商品、服务促销或其他商业信息。\n" +
                "2、本公司在本服务中可能提供与其他互联网之网站站点或资源的链接，本公司对存在或源于此类网站站点或资源的任何内容、广告、产品或其他资料不予保证或负责；如该链接所载的内容或搜索引擎所提供之链接的内容侵犯用户权利，本公司声明与上述内容无关，且不承担除法律有明确规定外的责任。");
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
//                enterHome();
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("同意", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                enterHome();
            }
        });
        builder.setNegativeButton("拒绝", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    private void enterHome() {
        if(SharedPreferencesUtils.getBoolean(SplashActivity.this,"isLogin",false)){
//            DemoCache.setAccount(account);
            MyMainActivity.start(SplashActivity.this, null);
            finish();
        }else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
//        DemoCache.setAccount(account);
//        SharedPreferencesUtils.setString(LoginActivity.this, "grade", userinfoBean.getGrade());
//        SharedPreferencesUtils.setInt(LoginActivity.this, "account_id", userinfoBean.getId());
//        saveLoginInfo(account, token);
//        SharedPreferencesUtils.setBoolean(LoginActivity.this,"isLogin",true);
//        // 初始化消息提醒配置
//        initNotificationConfig();
//        // 进入主界面
//        MyMainActivity.start(LoginActivity.this, null);
    }

    private void install(File t) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setDataAndType(Uri.fromFile(t),
                "application/vnd.android.package-archive");
        startActivity(intent);
    }

//    private void creatVersionUpdate(String pkName, String versionName) {
//        Update update = new Update();
//        update.setPackageName(pkName);
//        update.setVersion(versionName);
//        update.setUpdate(false);
//        update.save(new SaveListener<String>() {
//            @Override
//            public void done(String s, BmobException e) {
//                if (e == null) {
//                    enterHome();
//                } else {
//                    Log.e(TAG, e.getErrorCode() + e.getMessage());
//                }
//            }
//        });
//    }
//    private String getAppInfo() {
//        try {
//            String pkName = this.getPackageName();
//            String versionName = this.getPackageManager().getPackageInfo(
//                    pkName, 0).versionName;
//            int versionCode = this.getPackageManager()
//                    .getPackageInfo(pkName, 0).versionCode;
//            return pkName + "   " + versionName + "  " + versionCode;
//        } catch (Exception e) {
//        }
//        return null;
//    }
}
