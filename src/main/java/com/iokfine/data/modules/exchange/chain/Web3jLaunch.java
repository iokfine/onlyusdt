package com.iokfine.data.modules.exchange.chain;


import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.AtomicDouble;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.crypto.*;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Web3jLaunch {

    /**
     * path路径
     */
    private final static ImmutableList<ChildNumber> BIP44_ETH_ACCOUNT_ZERO_PATH =
            ImmutableList.of(new ChildNumber(44, true), new ChildNumber(60, true),
                    ChildNumber.ZERO_HARDENED, ChildNumber.ZERO);

    private static int radom(int max) {
        double random = Math.random();
        return (int) (random * max);
    }

    /**
     * 连接web3 节点
     */
//    private final static Web3j web3j = Web3j.build(new HttpService("https://rpc.kiln.themerge.dev"));
    private final static Web3j web3j = Web3j.build(new HttpService("https://nunki.htznr.fault.dev/rpc"));

    public static void main(String[] args) throws Exception {

        ExcelReader reader = ExcelUtil.getReader("D:\\ethtools\\ethtools\\1600_2.xlsx");
//        List<Address> addressList = reader.readAll(Address.class);
        List<Address> addressList = reader.read(0, 1, 1600, Address.class);

        doGetTotalETH(addressList);
        //创建钱包
        //createWallet();
//        doTask();
//        doGetETHTranactionTask();
//        doGetETHCall10Task();
//        topup();
    }

//

    private static void doGetTotalETH(List<Address> addressList) throws IOException {

        ThreadPoolExecutor ethExecutor = new ThreadPoolExecutor(8, 8, 0,
                TimeUnit.NANOSECONDS, new ArrayBlockingQueue<>(addressList.size()), new ThreadFactoryBuilder().setNameFormat("do-task-%d").build());

        AtomicDouble atomicDouble = new AtomicDouble(0);
        for (Address address : addressList) {
            ethExecutor.execute(() -> {
                BigInteger balance = null;
                try {
                    balance = web3j.ethGetBalance(address.getAddress(), DefaultBlockParameterName.PENDING).send().getBalance();
                    BigDecimal bigDecimal = Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER);
                    double v = atomicDouble.addAndGet(bigDecimal.doubleValue());

                    BigInteger nonce = web3j.ethGetTransactionCount(address.getAddress(), DefaultBlockParameterName.PENDING).send().getTransactionCount();
                    BigInteger last = web3j.ethGetTransactionCount(address.getAddress(), DefaultBlockParameterName.LATEST).send().getTransactionCount();

                    System.out.println(String.format("%s|%s|%s|%s|%s|%s|", addressList.indexOf(address), address.getAddress(), bigDecimal.toString(), v, nonce, last));
//                    log.info("{}|{}|{}|{}|", addressList.indexOf(address),address.getAddress(),bigDecimal.toString(),atomicDouble.toString());
                } catch (IOException e) {
//                    e.printStackTrace();
                    log.info("有一个报错了 {}", e.getMessage());
                }

            });

        }
        ethExecutor.shutdown();

    }

    private static void topup() throws IOException {
//        String[] addrs =  {"0xBFD113A96194D02bDfD202D62157C0c089eF8b67","0x1a09e8fc0BabF56FC02d8516673486614a406B28","0xFE4d1CB7e8B096F4783AF1c26368C21B69bb5E4B","0x8f2f74BcbdfB902EcAC7375753dD7f726Aae3461","0xd89339A2F7A0D9BF50f2017a3b2069ccb0D19EF2","0xDC4F2F9aCb36Da79C27dA4a1eb226B81b51cEb9B","0x6aB76817F1e06a9fA763CD96D116F84Fe2D8Eeca","0xb0E7165C38cc125Ef0E6fC7398A733F8D8Ac6804"};
//        ExcelReader reader = ExcelUtil.getReader("D:\\JetBrains\\workspace\\ethtools\\1600_2.xlsx");
//        ExcelReader reader = ExcelUtil.getReader("D:\\JetBrains\\workspace\\ethtools\\600_512.xlsx");
////        List<Address> addressList = reader.readAll(Address.class);
////        List<Address> addressList = reader.read(0, 449, 600, Address.class);
//
//        for (String address : addrs) {
//            try {
//                signETHTransaction("0x78003bA8C8ccc0F6726BE945DfC90Ce0162E1B68","36bc677236684bb2500e4c7a668a315a46cd7cb504cd26f5ce890f7c5abf641c",address,"18","100");
//            }catch (Exception e){
//                log.error(" e : ",e.getMessage());
//            }
//        }

    }


    /**
     * ETH转账
     *
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void signETHTransaction(String addr, String privateKey, String to, String amount, String gasPriceStr) throws IOException, ExecutionException, InterruptedException {

        //查询地址交易编号
        BigInteger nonce = web3j.ethGetTransactionCount(addr, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        BigInteger last = web3j.ethGetTransactionCount(addr, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        //支付的矿工费
        BigInteger gasPrice;
        if (StringUtils.isBlank(gasPriceStr)) {
            gasPrice = web3j.ethGasPrice().send().getGasPrice();
        } else {
            gasPrice = Convert.toWei(gasPriceStr, Convert.Unit.GWEI).toBigInteger();
        }
        BigInteger gasLimit = new BigInteger("210000");
        BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        //签名交易
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, amountWei, "");
        Credentials credentials = Credentials.create(privateKey);
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, 1337802, credentials);
        //广播交易
        String hash = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
        BigInteger balance = web3j.ethGetBalance(addr, DefaultBlockParameterName.PENDING).send().getBalance();

        log.info("|{}|{}|转帐|{}|{}|{}|{}|{}|{}|", addr, Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER), nonce, last,
                Convert.fromWei(web3j.ethGasPrice().send().getGasPrice().toString(), Convert.Unit.GWEI),
                to, amount, hash);

    }


    /**
     * ETH代币转账
     *
     * @throws IOException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void signTokenTransaction(String from, String privateKey, String coinAddress, String gasPriceStr) throws IOException, ExecutionException, InterruptedException {

        //查询地址交易编号
        BigInteger nonce = web3j.ethGetTransactionCount(from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        //支付的矿工费
        BigInteger gasPrice;
        if (StringUtils.isBlank(gasPriceStr)) {
            gasPrice = web3j.ethGasPrice().send().getGasPrice();
        } else {
            gasPrice = Convert.toWei(gasPriceStr, Convert.Unit.GWEI).toBigInteger();
        }
        BigInteger gasLimit = new BigInteger("210000");

        Credentials credentials = Credentials.create(privateKey);

        //封装转账交易
        Function function = new Function(
                "gm",
                Arrays.<Type>asList(),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        //签名交易
        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, coinAddress, data);
        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, 1337802, credentials);
        //广播交易
        String hash = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
        log.info("|{}|调用合约|{}|{}|", from, nonce, hash);
    }

//
//    public static void createContract(String addr, String privateKey, String gasPriceStr) throws IOException, ExecutionException, InterruptedException {
//
//        //查询地址交易编号
//        BigInteger nonce = web3j.ethGetTransactionCount(addr, DefaultBlockParameterName.PENDING).send().getTransactionCount();
//        BigInteger last = web3j.ethGetTransactionCount(addr, DefaultBlockParameterName.LATEST).send().getTransactionCount();
//        //支付的矿工费
//        BigInteger gasPrice;
//        if (StringUtils.isBlank(gasPriceStr)) {
//            gasPrice = web3j.ethGasPrice().send().getGasPrice();
//        } else {
//            gasPrice = Convert.toWei(gasPriceStr, Convert.Unit.GWEI).toBigInteger();
//        }
//        BigInteger gasLimit = new BigInteger("210000");
//
//        //广播交易
//        RawTransaction rawTransaction = RawTransaction.createContractTransaction(nonce, gasPrice, gasLimit, new BigInteger("0"),
//                "0x608060405234801561001057600080fd5b5061011d806100206000396000f3fe6080604052348015600f57600080fd5b506004361060285760003560e01c8063c0129d4314602d575b600080fd5b603360ab565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101560715780820151818401526020810190506058565b50505050905090810190601f168015609d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b60606040518060400160405280600c81526020017f48656c6c6f20676d2e78797a000000000000000000000000000000000000000081525090509056fea265627a7a7231582099ffbfce2e85de0d8ef1432aa5ba452cdd09157df16e730952fed34fe8c2abb164736f6c63430005110032");
//        Credentials credentials = Credentials.create(privateKey);
//        byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, 1337802, credentials);
//        String hash = web3j.ethSendRawTransaction(Numeric.toHexString(signMessage)).sendAsync().get().getTransactionHash();
//        BigInteger balance = web3j.ethGetBalance(addr, DefaultBlockParameterName.PENDING).send().getBalance();
//        log.info("|{}|{}|创约|{}|{}|{}|{}|", addr, Convert.fromWei(new BigDecimal(balance), Convert.Unit.ETHER), nonce, last,
//                Convert.fromWei(web3j.ethGasPrice().send().getGasPrice().toString(), Convert.Unit.GWEI), hash);
//    }


//    private static void doGetETHTranactionTask() throws IOException, InterruptedException {
//        ExcelReader reader = ExcelUtil.getReader("D:\\JetBrains\\workspace\\ethtools\\1600_2.xlsx");
//        List<Address> addressList = reader.read(0, 50, 1200, Address.class);
////        List<Address> addressList = Arrays.asList(addrs);
//        ThreadPoolExecutor ethExecutor = new ThreadPoolExecutor(16, 16, 0,
//                TimeUnit.NANOSECONDS, new ArrayBlockingQueue<>(32), new ThreadFactoryBuilder().setNameFormat("do-task-%d").build());
//        AtomicInteger atomicInteger = new AtomicInteger(0);
//            AtomicBoolean flag = new AtomicBoolean(true);
//            for (Address address : addressList) {
//                ethExecutor.execute(() -> {
//                    try {
//                        String addr = address.getAddress();
//                        String privateKey = address.getPrivateKey();
//                        String to = addressList.get(radom(radom(addressList.size()))).getAddress();
//                        String amount = RandomUtil.randomBigDecimal(new BigDecimal("0.00001"), new BigDecimal("0.001")).setScale(6, RoundingMode.HALF_UP).toString();
//                        signETHTransaction(addr, privateKey, to, amount,"800");
//                    } catch (Exception e) {
//                        log.error("报错了 {}", e.getMessage());
//                    } finally {
//                        if (address.equals(addressList.get(addressList.size() - 1))) {
//                            flag.set(false);
//                        }
//                    }
//                });
//            }
//
//        }
//
//
//    private static void doGetETHCall10Task() throws IOException, InterruptedException {
////        ExcelReader reader = ExcelUtil.getReader("D:\\JetBrains\\workspace\\ethtools\\1600_2.xlsx");
//        ExcelReader reader = ExcelUtil.getReader("D:\\JetBrains\\workspace\\ethtools\\600_512.xlsx");
////        List<Address> addressList = reader.read(0, 50, 1200, Address.class);
//        List<Address> addressList = reader.read(0, 1, 448, Address.class);
//        ThreadPoolExecutor ethExecutor = new ThreadPoolExecutor(8, 8, 0,
//                TimeUnit.NANOSECONDS, new ArrayBlockingQueue<>(addressList.size()), new ThreadFactoryBuilder().setNameFormat("do-task-%d").build());
//        AtomicInteger atomicInteger = new AtomicInteger(0);
//        String[] contracts = {
//                "0xB00Bca0BE4D84763B61198ff5c5cEA3978CAa7E6",
//                "0xD960C05292e47A5E7B95Cd1af076665a27a2A008",
//                "0xd04BFd437619ea825bF1c21db7C415B56163d8A0",
//                "0xA8534Df188428Fa630794f5D8Ff1e327B5F26A99",
//                "0xEB901668d115b5Aa524d5dbF29F5B515bd8884F2",
//                "0xD38B2157e5CA8Fa3E618FB712267A4D2dBc5A11E",
//                "0x2fBB78d12b5A486244e3640F7aFb710382603D72",
//                "0x100732A5D9964DC4DF0dFdaAB2d185251839d624",
//                "0x9A2E6fBeb5020E4d478172397040Be2162DFc7b9",
//                "0x0e46487e85c7E93A35A2f8966f07aE414318fac4",
//                "0x84966b94724952984417DCBdA4a674022a688cAb",
//        };
//        for (String contract : contracts) {
//            AtomicBoolean flag = new AtomicBoolean(true);
//            for (Address address : addressList) {
//                ethExecutor.execute(() -> {
//                    try {
//                        String addr = address.getAddress();
//                        String privateKey = address.getPrivateKey();
//                        signTokenTransaction(addr, privateKey,contract,"800");
//                    } catch (Exception e) {
//                        log.error("报错了 {}", e.getMessage());
//                    } finally {
//                        if (address.equals(addressList.get(addressList.size() - 1))) {
//                            log.info("一轮结束");
//                            flag.set(false);
//                        }
//                    }
//                });
//            }
//            while (flag.get()) {
//                Thread.sleep(1000);
//            }
//            log.info("下一轮");
//        }
//
//        System.out.println(atomicInteger.get());
//    }
}
//    private static void doTask() throws InterruptedException {
//        ExcelReader reader = ExcelUtil.getReader("D:\\JetBrains\\workspace\\ethtools\\600_512.xlsx");
//        List<Address> addressList = reader.read(0, 1, 448, Address.class);
////        List<Address> addressList = reader.readAll(Address.class);
//        ThreadPoolExecutor ethExecutor = new ThreadPoolExecutor(8, 8, 0,
//                TimeUnit.NANOSECONDS, new ArrayBlockingQueue<>(addressList.size()), new ThreadFactoryBuilder().setNameFormat("do-task-%d").build());
//
//        for (int i = 0; i < 70; i++) {
//            long start = System.currentTimeMillis();
//            AtomicBoolean flag = new AtomicBoolean(true);
//            for (Address address : addressList) {
//                ethExecutor.execute(() -> {
//                    try {
////                        log.info("index {}", addressList.indexOf(address));
//                        String addr = address.getAddress();
//                        String privateKey = address.getPrivateKey();
//                        String to = addressList.get(radom(radom(addressList.size()))).getAddress();
//                        String amount = RandomUtil.randomBigDecimal(new BigDecimal("0.00001"), new BigDecimal("0.001")).setScale(6, RoundingMode.HALF_UP).toString();
//                        String gasPriceStr = "400";
//                        signETHTransaction(addr, privateKey, to, amount, gasPriceStr);
//                        createContract(addr, privateKey, gasPriceStr);
////                        log.info("余额 {}", balance);
//                    } catch (Exception e) {
//                        log.error("报错了 {}", e.getMessage());
//                    } finally {
//                        if (address.equals(addressList.get(addressList.size() - 1))) {
//                            flag.set(false);
//                        }
//                    }
//                });
//            }
//            while (flag.get()) {
//                Thread.sleep(1000);
//            }
//            log.info("当前第{}轮， 下一轮 本轮耗时 {}ms", i, System.currentTimeMillis() - start);
//
//        }
//    }
