/*
 * Copyright 2025-2026 The FairScan authors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.fairscan.app.data

/**
 * Expected Git blob hash (SHA-1 of "blob <size>\u0000<content>") and exact size of each
 * Tesseract language file, taken from the pinned [TESSDATA_COMMIT] of
 * https://github.com/tesseract-ocr/tessdata_fast. Downloads are fetched from that exact
 * commit and rejected if size or hash differ.
 *
 * To update: pick a new commit, then regenerate this table from
 * https://api.github.com/repos/tesseract-ocr/tessdata_fast/git/trees/<commit>
 */
data class OcrFileHash(val gitBlobSha1: String, val size: Long)

const val TESSDATA_COMMIT = "87416418657359cb625c412a48b6e1d6d41c29bd"

val OCR_FILE_HASHES: Map<String, OcrFileHash> = mapOf(
    "afr" to OcrFileHash("09e69cf5478cd6a0fb3ec5e7e7fa95a04f5cafb3", 2652786L),
    "amh" to OcrFileHash("7a302b2bf6306c34177f594e0824eafc48906965", 5470094L),
    "ara" to OcrFileHash("c8d129c67821c1592cac46836686b057ecc203dc", 1432056L),
    "asm" to OcrFileHash("086035c6eacd5788c1f06d58c250023cee2aeb28", 2045427L),
    "aze_cyrl" to OcrFileHash("e2b1b953a5b8db9247c6ddfcb58f062dc0b3b574", 1943672L),
    "aze" to OcrFileHash("27bc73edf08e5e5f0c3e43c315c5c6d965c2f075", 3524799L),
    "bel" to OcrFileHash("65d83dad535f876de31847f5fa2ff1bdd57c7bab", 3692098L),
    "ben" to OcrFileHash("7e9054d1de309714009a85555f5276168d41af9c", 855841L),
    "bod" to OcrFileHash("7bb5029466eaef46aad59fc23b96648df0e4f176", 1966440L),
    "bos" to OcrFileHash("95bed5c32e3759347db0471569303f06e3485f42", 2498242L),
    "bre" to OcrFileHash("c01691329561938fb7d491ea7611c591202952dd", 6335007L),
    "bul" to OcrFileHash("1e65588d127d918ef3ba95b785b446f15827d60f", 1675212L),
    "cat" to OcrFileHash("3fcd350dd2bb76503de90249111fff4e3c6aa0f0", 1146012L),
    "ceb" to OcrFileHash("5a1b96928e54b175db96f00d2aa84b5878d301b9", 716214L),
    "ces" to OcrFileHash("dd6fc79a62f9fcd1c7c6da06b31bb4fdb10dc173", 3795684L),
    "chi_sim" to OcrFileHash("388bac276d033d06e5ed5ba7a7ad14ae58f97dab", 2469156L),
    "chi_sim_vert" to OcrFileHash("f9898e91068a9d04bcacf6d2a244da40e9f30c0c", 1927902L),
    "chi_tra" to OcrFileHash("1955cd8b8d076b898616e9c2c1ace65b43105356", 2366642L),
    "chi_tra_vert" to OcrFileHash("94bc5dbd7fa25dd89ac9c5bd18f3fc6d8409aa0e", 1824756L),
    "chr" to OcrFileHash("e0799a2b888466e5bc2ca60f1f2fb4a7ff4c6990", 374944L),
    "cos" to OcrFileHash("3105b7fb0cd6c26f4fb144ec5d594430e88927cb", 2299099L),
    "cym" to OcrFileHash("900cf17f74514934f87e9a8df3b29cb16b5606fe", 2208919L),
    "dan" to OcrFileHash("85501b582937493f9ae6a64ccd0a6bb0a2ea33d5", 2580059L),
    "deu" to OcrFileHash("97ed7b2b60f2771c07040660ef0f6daf596dc7bf", 1525436L),
    "div" to OcrFileHash("53b46df7d6ae94f75913d6377182867ea09d950b", 1774535L),
    "dzo" to OcrFileHash("3c31175be56ab4db42af614f384ea52ce949082c", 449596L),
    "ell" to OcrFileHash("ed98ae1a88d84414da316e6eeab3232f2c68639b", 1419514L),
    "eng" to OcrFileHash("bbef4675053b5b468cdb477053e28b1c698ba08e", 4113088L),
    "enm" to OcrFileHash("e49b5b86da60f2e728ff2b7f4357692ac32b6a6d", 3101551L),
    "epo" to OcrFileHash("235fc67adf52f43d34b409d44d426d560aefc8e3", 4728393L),
    "est" to OcrFileHash("a2b52c3388358e6dfc4eb8d64d8fc6afbb3630ea", 4458101L),
    "eus" to OcrFileHash("6247f975443ec82c219c39e0b5ea76b4833c82fe", 5175921L),
    "fao" to OcrFileHash("1b845e73759785908aef57161ae4c86a70cce622", 3439772L),
    "fas" to OcrFileHash("1a17ee02076107e73abd15d2038389150c8f824f", 431500L),
    "fil" to OcrFileHash("785f80decce6ddb6b50bd99f4840414359c806ff", 1841715L),
    "fin" to OcrFileHash("73f92402f1891b9066e0e145f245145a2402105c", 7865732L),
    "fra" to OcrFileHash("d9e2b2160be0d1ca3b8f1bf2730fae476ef3b4a6", 1130365L),
    "frm" to OcrFileHash("ed06039efef821b78f22c86abb6c5aea7f02461e", 2024948L),
    "fry" to OcrFileHash("17199d2c33e201d6ad4003a6c86db5f8f273ddc1", 1906018L),
    "gla" to OcrFileHash("e8bae7f1863dd1872ab7d2b2e7f319874c480e60", 3068307L),
    "gle" to OcrFileHash("a7abc7be9aadc862b31ab7c7bc672051cd0245fb", 1181824L),
    "glg" to OcrFileHash("7bc00b38f40f8de1575397f7d05387e5fbbacca4", 2554555L),
    "grc" to OcrFileHash("e94990006fc18e88ab120e7ca6681cc3b290269b", 2246328L),
    "guj" to OcrFileHash("151b9aad2945e3103118e5d36eee288f631d985a", 1418394L),
    "hat" to OcrFileHash("30d3baeaeef791781d782e58feb415b25921df3f", 1976902L),
    "heb" to OcrFileHash("7356caf3cddc9c867fe6727e17726727b8284608", 961404L),
    "hin" to OcrFileHash("a8f0aaec09378115b12a2c6287c57695d082aea2", 1122751L),
    "hrv" to OcrFileHash("6dd4f8638a58eb0663e8f9a80a355c505dcf01a6", 4103348L),
    "hun" to OcrFileHash("5d24fa8e922d6b57853dcb0cc490060c76643cab", 5296273L),
    "hye" to OcrFileHash("ec2e2f3adea785e8964b9771871c44349a6e1b40", 3463717L),
    "iku" to OcrFileHash("6eab87ffe818b5f89baa869c458cb0a6dbed6b2d", 2802266L),
    "ind" to OcrFileHash("3013f50cd2af9aceca00e865ed889144865f0848", 1122661L),
    "isl" to OcrFileHash("20d3bdf8cb8f2289a1f45aa9cdf4ddf2a22e7462", 2278973L),
    "ita" to OcrFileHash("edbffbe0e51892b240630b5eeabededa7e7c9c4f", 2701314L),
    "jav" to OcrFileHash("3155b8184d50af28dbf8dcbd972e26b388ab1581", 2982741L),
    "jpn" to OcrFileHash("c4178f89991bde90b7fdc647e3e1901868423bd0", 2471260L),
    "jpn_vert" to OcrFileHash("43f38defe4b83588876d0d7a6dee928be1b73af0", 3037480L),
    "kan" to OcrFileHash("78252baeed24bcf327e62226c321c60242108a24", 3608331L),
    "kat_old" to OcrFileHash("f4ae5ab4b3fe4bb3226639d9b23fa71513a40ec8", 423167L),
    "kat" to OcrFileHash("1a3ae11fdd248891dd3fc74f156a0ce246ec1a15", 2524769L),
    "kaz" to OcrFileHash("f889e2b8ffd093d4f2dff8ffca2fa0b73296c028", 4734644L),
    "khm" to OcrFileHash("d466dc2a7e35bf90cb7b54364985703728398687", 1446926L),
    "kir" to OcrFileHash("3e04f2954f33dd021b95de4befffb68904d9e142", 9928497L),
    "kmr" to OcrFileHash("d1afefb7c5a67e273c6612aba77753021506b557", 3568615L),
    "kor" to OcrFileHash("60986d44497689f3abace0b148199476d93292e1", 1677415L),
    "kor_vert" to OcrFileHash("ea0a4963591f153a21c8f237de2a1c644b57f2b9", 1113715L),
    "lao" to OcrFileHash("10bd41ae9352889f85a489eb3183eb0cbebc2516", 6386744L),
    "lat" to OcrFileHash("30dab6a24339f262d1eb21a3ee272d16c46aeba3", 3187463L),
    "lav" to OcrFileHash("71d925c8b6c73980157fd0128412b46e6e8396b5", 2717247L),
    "lit" to OcrFileHash("af9e2b3cd6a65650206b7d89f1b93b51683b9692", 3154896L),
    "ltz" to OcrFileHash("c1ac5b922bbcad34b2654ba471c0a752c0397404", 2606426L),
    "mal" to OcrFileHash("70e96f02cbc2f26630225112cd323a7f0b6ccdf3", 5275996L),
    "mar" to OcrFileHash("79138fadcdb256ec7b59e3bf9df18bd5fb492bee", 2118233L),
    "mkd" to OcrFileHash("fb0b245fb9ec61696e818887a70d8413b2914905", 1600188L),
    "mlt" to OcrFileHash("4242f9cd016987f16feb06973e988eced9604787", 2308796L),
    "mon" to OcrFileHash("2ef212c519edc66ff2508f48dbe1bb836a4ea274", 2137042L),
    "mri" to OcrFileHash("eab6b1a98e4b6ed510d6443a5e3d14519da3b338", 862973L),
    "msa" to OcrFileHash("f0e2b8bef0a681f09baac99a418529205ee80201", 1747801L),
    "mya" to OcrFileHash("cf28ddca604dc7f46bc34f2f4c6a193a0706c03b", 4640561L),
    "nep" to OcrFileHash("948e365b55f9fe8179dde2ff0134feffb210496e", 1002911L),
    "nld" to OcrFileHash("53ce95a9c2488a9a0228939944a184157373d0ae", 6050296L),
    "nor" to OcrFileHash("9209df2c9904c127437c07e16c287cdefd024e7c", 3610079L),
    "oci" to OcrFileHash("4017d4405b09333f22a3de2a4d30cf009bdb4a9b", 6322087L),
    "ori" to OcrFileHash("ae0f9ed13d5c9ad4a4de57c7266c434b83a9f583", 1480066L),
    "pan" to OcrFileHash("d4f00533a6c76ce4045aa6ad40b39f47eeaa333f", 497721L),
    "pol" to OcrFileHash("2382f568f0ce0c4c0a7f4380a291255514e5e97f", 4765518L),
    "por" to OcrFileHash("e9f373e95c66b4bf557c263721ef31f78e5bc301", 1982756L),
    "pus" to OcrFileHash("220cdf92efdc76a227439c0b8734196af9743b98", 1772087L),
    "que" to OcrFileHash("d80c1f308973b3129183843759f23fdcb1c5c2be", 5026336L),
    "ron" to OcrFileHash("04cd2bc7b471431354e9446e973f3472f442e189", 2376323L),
    "rus" to OcrFileHash("b146cb2263acbc6383f8e92ea0ce759537687bb8", 3861738L),
    "san" to OcrFileHash("3df043f3247fd41a21ac47c3b199bc87643e984b", 12404677L),
    "sin" to OcrFileHash("d0c8584e78c44666efcd7fbd47940a8a1121550d", 1727413L),
    "slk" to OcrFileHash("ca99e3e82f69a0e16b48cdfec3c4e706758269f2", 4427661L),
    "slv" to OcrFileHash("835954e891897d3c8b63db48cb5c2712ef2e6b11", 3003829L),
    "snd" to OcrFileHash("1ef6548d22977dc1f236be879dce3a62ac6a47ed", 1694035L),
    "spa" to OcrFileHash("72e901f13ca52cfe34cf239a368b9ed3c0ddaf26", 2294433L),
    "sqi" to OcrFileHash("9624fbf33452a95dd5a0a3a93fe9876108104895", 1874705L),
    "srp_latn" to OcrFileHash("5f81e591adad58a179a2d371aacccaf0801f93a2", 3281787L),
    "srp" to OcrFileHash("530fb4b1cb27f369bbee7eb0add89bfd73d6f15c", 2149931L),
    "sun" to OcrFileHash("894891425c1fe302ce1d8f115a3c00967c6c2832", 1369500L),
    "swa" to OcrFileHash("633d73b35869600452eec58dcd9036cc702c3e48", 2167651L),
    "swe" to OcrFileHash("684e1fa66e4a55b983febf5f9ed04f26ad946e59", 4167034L),
    "syr" to OcrFileHash("462aefc8a1485ae4c38db82f7f50fd105bf30195", 2207208L),
    "tam" to OcrFileHash("fa1793d68f9aba8bf191e8ff1036e2271d288e44", 3237963L),
    "tat" to OcrFileHash("9485ca27d48bb161a47584f8381420462ef6fa27", 1072896L),
    "tel" to OcrFileHash("ee8a33b0f658fc75b8cbfe8c10413eac4f41e6ad", 2769654L),
    "tgk" to OcrFileHash("044766728ce9cb2cc73976175b970a4cea9a870a", 2602685L),
    "tha" to OcrFileHash("ea28de3985668cac00d261c5b91f023ea66e19cd", 1072600L),
    "tir" to OcrFileHash("55e62eb0297e07360db5e9dc5dd86aab8e0a1e9c", 378822L),
    "ton" to OcrFileHash("9eff0fd002798089f5f7d8307be968a36f1ce433", 947249L),
    "tur" to OcrFileHash("1f0b331ef29f50ceb7c44da58e38c7ec42314d0a", 4550554L),
    "uig" to OcrFileHash("36124d6b0ed1c2654a13551203c25b44b4013978", 2794272L),
    "ukr" to OcrFileHash("d02cf90b2911a3da66dac6ae380bcefb18dd7663", 3825102L),
    "urd" to OcrFileHash("715a159d4abea25294c82971be28be215b3d1c4a", 1398718L),
    "uzb_cyrl" to OcrFileHash("c2ff8b9c282a9712b5f24970927377bc18dea7cf", 1563501L),
    "uzb" to OcrFileHash("109abc765273b2c9eabd55781f0a619320c80007", 6470703L),
    "vie" to OcrFileHash("e54a5912fb634c581c4a45ae9f37cbc021ec7140", 531275L),
    "yid" to OcrFileHash("6349588bbabf99f8a812e8231c98c015ea7b60c6", 545606L),
    "yor" to OcrFileHash("3e0cd582f65f17844039173057dfc015bf19f61d", 963400L),
)
