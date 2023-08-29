package com.plgchain.app.plingaHelper.contracts.defi.nexi.v1;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint112;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.9.4.
 */
@SuppressWarnings("rawtypes")
public class NexiSwapPair extends Contract {
    public static final String BINARY = "60806040526001600c5534801561001557600080fd5b5060405146908060526123768239604080519182900360520182208282018252600c83526b4e65786953776170204c507360a01b6020938401528151808301835260018152603160f81b908401528151808401919091527fa21b6568ca1fcba62bc67d994bd22592bdb7eb12820b1a117816d1455d2ebd5f818301527fc89efdaa54c0f20c7adf612882df0950f5a951637e0307cdcb4c672f298b8bc6606082015260808101949094523060a0808601919091528151808603909101815260c09094019052825192019190912060035550600580546001600160a01b0319163317905561226f806101076000396000f3fe608060405234801561001057600080fd5b50600436106101a95760003560e01c80636a627842116100f9578063ba9a7a5611610097578063d21220a711610071578063d21220a714610534578063d505accf1461053c578063dd62ed3e1461058d578063fff6cae9146105bb576101a9565b8063ba9a7a56146104fe578063bc25cf7714610506578063c45a01551461052c576101a9565b80637ecebe00116100d35780637ecebe001461046557806389afcb441461048b57806395d89b41146104ca578063a9059cbb146104d2576101a9565b80636a6278421461041157806370a08231146104375780637464fc3d1461045d576101a9565b806323b872dd116101665780633644e515116101405780633644e515146103cb578063485cc955146103d35780635909c0d5146104015780635a3d549314610409576101a9565b806323b872dd1461036f57806330adf81f146103a5578063313ce567146103ad576101a9565b8063022c0d9f146101ae57806306fdde031461023c5780630902f1ac146102b9578063095ea7b3146102f15780630dfe16811461033157806318160ddd14610355575b600080fd5b61023a600480360360808110156101c457600080fd5b8135916020810135916001600160a01b0360408301351691908101906080810160608201356401000000008111156101fb57600080fd5b82018360208201111561020d57600080fd5b8035906020019184600183028401116401000000008311171561022f57600080fd5b5090925090506105c3565b005b610244610b11565b6040805160208082528351818301528351919283929083019185019080838360005b8381101561027e578181015183820152602001610266565b50505050905090810190601f1680156102ab5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6102c1610b39565b604080516001600160701b03948516815292909316602083015263ffffffff168183015290519081900360600190f35b61031d6004803603604081101561030757600080fd5b506001600160a01b038135169060200135610b63565b604080519115158252519081900360200190f35b610339610b7a565b604080516001600160a01b039092168252519081900360200190f35b61035d610b89565b60408051918252519081900360200190f35b61031d6004803603606081101561038557600080fd5b506001600160a01b03813581169160208101359091169060400135610b8f565b61035d610c29565b6103b5610c4d565b6040805160ff9092168252519081900360200190f35b61035d610c52565b61023a600480360360408110156103e957600080fd5b506001600160a01b0381358116916020013516610c58565b61035d610cdb565b61035d610ce1565b61035d6004803603602081101561042757600080fd5b50356001600160a01b0316610ce7565b61035d6004803603602081101561044d57600080fd5b50356001600160a01b0316610fe6565b61035d610ff8565b61035d6004803603602081101561047b57600080fd5b50356001600160a01b0316610ffe565b6104b1600480360360208110156104a157600080fd5b50356001600160a01b0316611010565b6040805192835260208301919091528051918290030190f35b6102446113b5565b61031d600480360360408110156104e857600080fd5b506001600160a01b0381351690602001356113dc565b61035d6113e9565b61023a6004803603602081101561051c57600080fd5b50356001600160a01b03166113ef565b610339611559565b610339611568565b61023a600480360360e081101561055257600080fd5b506001600160a01b03813581169160208101359091169060408101359060608101359060ff6080820135169060a08101359060c00135611577565b61035d600480360360408110156105a357600080fd5b506001600160a01b0381358116916020013516611778565b61023a611795565b600c5460011461060d576040805162461bcd60e51b815260206004820152601060248201526f13995e1a54ddd85c0e881313d0d2d15160821b604482015290519081900360640190fd5b6000600c55841515806106205750600084115b61065b5760405162461bcd60e51b81526004018080602001828103825260248152602001806122176024913960400191505060405180910390fd5b600080610666610b39565b5091509150816001600160701b03168710801561068b5750806001600160701b031686105b6106dc576040805162461bcd60e51b815260206004820181905260248201527f4e657869537761703a20494e53554646494349454e545f4c4951554944495459604482015290519081900360640190fd5b60065460075460009182916001600160a01b0391821691908116908916821480159061071a5750806001600160a01b0316896001600160a01b031614155b610762576040805162461bcd60e51b81526020600482015260146024820152734e657869537761703a20494e56414c49445f544f60601b604482015290519081900360640190fd5b8a1561077357610773828a8d6118f6565b891561078457610784818a8c6118f6565b861561083f57886001600160a01b0316632631e83a338d8d8c8c6040518663ffffffff1660e01b815260040180866001600160a01b03166001600160a01b03168152602001858152602001848152602001806020018281038252848482818152602001925080828437600081840152601f19601f8201169050808301925050509650505050505050600060405180830381600087803b15801561082657600080fd5b505af115801561083a573d6000803e3d6000fd5b505050505b604080516370a0823160e01b815230600482015290516001600160a01b038416916370a08231916024808301926020929190829003018186803b15801561088557600080fd5b505afa158015610899573d6000803e3d6000fd5b505050506040513d60208110156108af57600080fd5b5051604080516370a0823160e01b815230600482015290519195506001600160a01b038316916370a0823191602480820192602092909190829003018186803b1580156108fb57600080fd5b505afa15801561090f573d6000803e3d6000fd5b505050506040513d602081101561092557600080fd5b5051925060009150506001600160701b0385168a90038311610948576000610957565b89856001600160701b03160383035b9050600089856001600160701b0316038311610974576000610983565b89856001600160701b03160383035b905060008211806109945750600081115b6109cf5760405162461bcd60e51b81526004018080602001828103825260238152602001806121a66023913960400191505060405180910390fd5b6000610a036109e584600263ffffffff611a9016565b6109f7876103e863ffffffff611a9016565b9063ffffffff611af316565b90506000610a1b6109e584600263ffffffff611a9016565b9050610a4c620f4240610a406001600160701b038b8116908b1663ffffffff611a9016565b9063ffffffff611a9016565b610a5c838363ffffffff611a9016565b1015610a9d576040805162461bcd60e51b815260206004820152600b60248201526a4e657869537761703a204b60a81b604482015290519081900360640190fd5b5050610aab84848888611b43565b60408051838152602081018390528082018d9052606081018c905290516001600160a01b038b169133917fd78ad95fa46c994b6551d0da85fc275fe613ce37657fb8d5e3d130840159d8229181900360800190a350506001600c55505050505050505050565b6040518060400160405280600c81526020016b4e65786953776170204c507360a01b81525081565b6008546001600160701b0380821692600160701b830490911691600160e01b900463ffffffff1690565b6000610b70338484611d07565b5060015b92915050565b6006546001600160a01b031681565b60005481565b6001600160a01b038316600090815260026020908152604080832033845290915281205460001914610c14576001600160a01b0384166000908152600260209081526040808320338452909152902054610bef908363ffffffff611af316565b6001600160a01b03851660009081526002602090815260408083203384529091529020555b610c1f848484611d69565b5060019392505050565b7f6e71edae12b1b97f4d1f60370fef10105fa2faae0126114a169c64845d6126c981565b601281565b60035481565b6005546001600160a01b03163314610cad576040805162461bcd60e51b81526020600482015260136024820152722732bc34a9bbb0b81d102327a92124a22222a760691b604482015290519081900360640190fd5b600680546001600160a01b039384166001600160a01b03199182161790915560078054929093169116179055565b60095481565b600a5481565b6000600c54600114610d33576040805162461bcd60e51b815260206004820152601060248201526f13995e1a54ddd85c0e881313d0d2d15160821b604482015290519081900360640190fd5b6000600c81905580610d43610b39565b50600654604080516370a0823160e01b815230600482015290519395509193506000926001600160a01b03909116916370a08231916024808301926020929190829003018186803b158015610d9757600080fd5b505afa158015610dab573d6000803e3d6000fd5b505050506040513d6020811015610dc157600080fd5b5051600754604080516370a0823160e01b815230600482015290519293506000926001600160a01b03909216916370a0823191602480820192602092909190829003018186803b158015610e1457600080fd5b505afa158015610e28573d6000803e3d6000fd5b505050506040513d6020811015610e3e57600080fd5b505190506000610e5d836001600160701b03871663ffffffff611af316565b90506000610e7a836001600160701b03871663ffffffff611af316565b90506000610e888787611e23565b60005490915080610ec557610eb16103e86109f7610eac878763ffffffff611a9016565b611f81565b9850610ec060006103e8611fd3565b610f14565b610f116001600160701b038916610ee2868463ffffffff611a9016565b81610ee957fe5b046001600160701b038916610f04868563ffffffff611a9016565b81610f0b57fe5b04612069565b98505b60008911610f535760405162461bcd60e51b81526004018080602001828103825260278152602001806121c96027913960400191505060405180910390fd5b610f5d8a8a611fd3565b610f6986868a8a611b43565b8115610f9957600854610f95906001600160701b0380821691600160701b90041663ffffffff611a9016565b600b555b6040805185815260208101859052815133927f4c209b5fc8ad50758f13e2e1088ba56a560dff690a1c6fef26394f4c03821c4f928290030190a250506001600c5550949695505050505050565b60016020526000908152604090205481565b600b5481565b60046020526000908152604090205481565b600080600c5460011461105d576040805162461bcd60e51b815260206004820152601060248201526f13995e1a54ddd85c0e881313d0d2d15160821b604482015290519081900360640190fd5b6000600c8190558061106d610b39565b50600654600754604080516370a0823160e01b815230600482015290519496509294506001600160a01b039182169391169160009184916370a08231916024808301926020929190829003018186803b1580156110c957600080fd5b505afa1580156110dd573d6000803e3d6000fd5b505050506040513d60208110156110f357600080fd5b5051604080516370a0823160e01b815230600482015290519192506000916001600160a01b038516916370a08231916024808301926020929190829003018186803b15801561114157600080fd5b505afa158015611155573d6000803e3d6000fd5b505050506040513d602081101561116b57600080fd5b50513060009081526001602052604081205491925061118a8888611e23565b600054909150806111a1848763ffffffff611a9016565b816111a857fe5b049a50806111bc848663ffffffff611a9016565b816111c357fe5b04995060008b1180156111d6575060008a115b6112115760405162461bcd60e51b81526004018080602001828103825260278152602001806121f06027913960400191505060405180910390fd5b61121b3084612081565b611226878d8d6118f6565b611231868d8c6118f6565b604080516370a0823160e01b815230600482015290516001600160a01b038916916370a08231916024808301926020929190829003018186803b15801561127757600080fd5b505afa15801561128b573d6000803e3d6000fd5b505050506040513d60208110156112a157600080fd5b5051604080516370a0823160e01b815230600482015290519196506001600160a01b038816916370a0823191602480820192602092909190829003018186803b1580156112ed57600080fd5b505afa158015611301573d6000803e3d6000fd5b505050506040513d602081101561131757600080fd5b5051935061132785858b8b611b43565b811561135757600854611353906001600160701b0380821691600160701b90041663ffffffff611a9016565b600b555b604080518c8152602081018c905281516001600160a01b038f169233927fdccd412f0b1252819cb1fd330b93224ca42612892bb3f4f789976e6d81936496929081900390910190a35050505050505050506001600c81905550915091565b6040518060400160405280600b81526020016a04e657869537761702d4c560ac1b81525081565b6000610b70338484611d69565b6103e881565b600c54600114611439576040805162461bcd60e51b815260206004820152601060248201526f13995e1a54ddd85c0e881313d0d2d15160821b604482015290519081900360640190fd5b6000600c55600654600754600854604080516370a0823160e01b815230600482015290516001600160a01b0394851694909316926114e892859287926114e3926001600160701b03169185916370a0823191602480820192602092909190829003018186803b1580156114ab57600080fd5b505afa1580156114bf573d6000803e3d6000fd5b505050506040513d60208110156114d557600080fd5b50519063ffffffff611af316565b6118f6565b600854604080516370a0823160e01b8152306004820152905161154f92849287926114e392600160701b90046001600160701b0316916001600160a01b038616916370a0823191602480820192602092909190829003018186803b1580156114ab57600080fd5b50506001600c5550565b6005546001600160a01b031681565b6007546001600160a01b031681565b428410156115c0576040805162461bcd60e51b815260206004820152601160248201527013995e1a54ddd85c0e8811561412549151607a1b604482015290519081900360640190fd5b6003546001600160a01b0380891660008181526004602090815260408083208054600180820190925582517f6e71edae12b1b97f4d1f60370fef10105fa2faae0126114a169c64845d6126c98186015280840196909652958d166060860152608085018c905260a085019590955260c08085018b90528151808603909101815260e08501825280519083012061190160f01b6101008601526101028501969096526101228085019690965280518085039096018652610142840180825286519683019690962095839052610162840180825286905260ff89166101828501526101a284018890526101c28401879052519193926101e280820193601f1981019281900390910190855afa1580156116db573d6000803e3d6000fd5b5050604051601f1901519150506001600160a01b038116158015906117115750886001600160a01b0316816001600160a01b0316145b611762576040805162461bcd60e51b815260206004820152601b60248201527f4e657869537761703a20494e56414c49445f5349474e41545552450000000000604482015290519081900360640190fd5b61176d898989611d07565b505050505050505050565b600260209081526000928352604080842090915290825290205481565b600c546001146117df576040805162461bcd60e51b815260206004820152601060248201526f13995e1a54ddd85c0e881313d0d2d15160821b604482015290519081900360640190fd5b6000600c55600654604080516370a0823160e01b815230600482015290516118ef926001600160a01b0316916370a08231916024808301926020929190829003018186803b15801561183057600080fd5b505afa158015611844573d6000803e3d6000fd5b505050506040513d602081101561185a57600080fd5b5051600754604080516370a0823160e01b815230600482015290516001600160a01b03909216916370a0823191602480820192602092909190829003018186803b1580156118a757600080fd5b505afa1580156118bb573d6000803e3d6000fd5b505050506040513d60208110156118d157600080fd5b50516008546001600160701b0380821691600160701b900416611b43565b6001600c55565b604080518082018252601981527f7472616e7366657228616464726573732c75696e74323536290000000000000060209182015281516001600160a01b0385811660248301526044808301869052845180840390910181526064909201845291810180516001600160e01b031663a9059cbb60e01b1781529251815160009460609489169392918291908083835b602083106119a35780518252601f199092019160209182019101611984565b6001836020036101000a0380198251168184511680821785525050505050509050019150506000604051808303816000865af19150503d8060008114611a05576040519150601f19603f3d011682016040523d82523d6000602084013e611a0a565b606091505b5091509150818015611a38575080511580611a385750808060200190516020811015611a3557600080fd5b50515b611a89576040805162461bcd60e51b815260206004820152601960248201527f4e657869537761703a205452414e534645525f4641494c454400000000000000604482015290519081900360640190fd5b5050505050565b6000811580611aab57505080820282828281611aa857fe5b04145b610b74576040805162461bcd60e51b815260206004820152601460248201527364732d6d6174682d6d756c2d6f766572666c6f7760601b604482015290519081900360640190fd5b80820382811115610b74576040805162461bcd60e51b815260206004820152601560248201527464732d6d6174682d7375622d756e646572666c6f7760581b604482015290519081900360640190fd5b6001600160701b038411801590611b6157506001600160701b038311155b611ba7576040805162461bcd60e51b81526020600482015260126024820152714e657869537761703a204f564552464c4f5760701b604482015290519081900360640190fd5b60085463ffffffff42811691600160e01b90048116820390811615801590611bd757506001600160701b03841615155b8015611beb57506001600160701b03831615155b15611c5c578063ffffffff16611c1985611c048661211f565b6001600160e01b03169063ffffffff61213116565b600980546001600160e01b03929092169290920201905563ffffffff8116611c4484611c048761211f565b600a80546001600160e01b0392909216929092020190555b600880546dffffffffffffffffffffffffffff19166001600160701b03888116919091176dffffffffffffffffffffffffffff60701b1916600160701b8883168102919091176001600160e01b0316600160e01b63ffffffff871602179283905560408051848416815291909304909116602082015281517f1c411e9a96e071241c2f21f7726b17ae89e3cab4c78be50e062b03a9fffbbad1929181900390910190a1505050505050565b6001600160a01b03808416600081815260026020908152604080832094871680845294825291829020859055815185815291517f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259281900390910190a3505050565b6001600160a01b038316600090815260016020526040902054611d92908263ffffffff611af316565b6001600160a01b038085166000908152600160205260408082209390935590841681522054611dc7908263ffffffff61215616565b6001600160a01b0380841660008181526001602090815260409182902094909455805185815290519193928716927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef92918290030190a3505050565b600080600560009054906101000a90046001600160a01b03166001600160a01b031663017e7e586040518163ffffffff1660e01b815260040160206040518083038186803b158015611e7457600080fd5b505afa158015611e88573d6000803e3d6000fd5b505050506040513d6020811015611e9e57600080fd5b5051600b546001600160a01b038216158015945091925090611f6d578015611f68576000611ee1610eac6001600160701b0388811690881663ffffffff611a9016565b90506000611eee83611f81565b905080821115611f65576000611f1c611f0d848463ffffffff611af316565b6000549063ffffffff611a9016565b90506000611f4183611f3586600363ffffffff611a9016565b9063ffffffff61215616565b90506000818381611f4e57fe5b0490508015611f6157611f618782611fd3565b5050505b50505b611f79565b8015611f79576000600b555b505092915050565b60006003821115611fc4575080600160028204015b81811015611fbe57809150600281828581611fad57fe5b040181611fb657fe5b049050611f96565b50611fce565b8115611fce575060015b919050565b600054611fe6908263ffffffff61215616565b60009081556001600160a01b038316815260016020526040902054612011908263ffffffff61215616565b6001600160a01b03831660008181526001602090815260408083209490945583518581529351929391927fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9281900390910190a35050565b6000818310612078578161207a565b825b9392505050565b6001600160a01b0382166000908152600160205260409020546120aa908263ffffffff611af316565b6001600160a01b038316600090815260016020526040812091909155546120d7908263ffffffff611af316565b60009081556040805183815290516001600160a01b038516917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef919081900360200190a35050565b6001600160701b0316600160701b0290565b60006001600160701b0382166001600160e01b0384168161214e57fe5b049392505050565b80820182811015610b74576040805162461bcd60e51b815260206004820152601460248201527364732d6d6174682d6164642d6f766572666c6f7760601b604482015290519081900360640190fdfe4e657869537761703a20494e53554646494349454e545f494e5055545f414d4f554e544e657869537761703a20494e53554646494349454e545f4c49515549444954595f4d494e5445444e657869537761703a20494e53554646494349454e545f4c49515549444954595f4255524e45444e657869537761703a20494e53554646494349454e545f4f55545055545f414d4f554e54a265627a7a7231582008cf9d3a8eb2a800136016e10125f8a531084d17a73f7515ab8356ef6579231864736f6c63430005100032454950373132446f6d61696e28737472696e67206e616d652c737472696e672076657273696f6e2c75696e7432353620636861696e49642c6164647265737320766572696679696e67436f6e747261637429";

    public static final String FUNC_DOMAIN_SEPARATOR = "DOMAIN_SEPARATOR";

    public static final String FUNC_MINIMUM_LIQUIDITY = "MINIMUM_LIQUIDITY";

    public static final String FUNC_PERMIT_TYPEHASH = "PERMIT_TYPEHASH";

    public static final String FUNC_ALLOWANCE = "allowance";

    public static final String FUNC_APPROVE = "approve";

    public static final String FUNC_BALANCEOF = "balanceOf";

    public static final String FUNC_BURN = "burn";

    public static final String FUNC_DECIMALS = "decimals";

    public static final String FUNC_FACTORY = "factory";

    public static final String FUNC_GETRESERVES = "getReserves";

    public static final String FUNC_INITIALIZE = "initialize";

    public static final String FUNC_KLAST = "kLast";

    public static final String FUNC_MINT = "mint";

    public static final String FUNC_NAME = "name";

    public static final String FUNC_NONCES = "nonces";

    public static final String FUNC_PERMIT = "permit";

    public static final String FUNC_PRICE0CUMULATIVELAST = "price0CumulativeLast";

    public static final String FUNC_PRICE1CUMULATIVELAST = "price1CumulativeLast";

    public static final String FUNC_SKIM = "skim";

    public static final String FUNC_SWAP = "swap";

    public static final String FUNC_SYMBOL = "symbol";

    public static final String FUNC_SYNC = "sync";

    public static final String FUNC_TOKEN0 = "token0";

    public static final String FUNC_TOKEN1 = "token1";

    public static final String FUNC_TOTALSUPPLY = "totalSupply";

    public static final String FUNC_TRANSFER = "transfer";

    public static final String FUNC_TRANSFERFROM = "transferFrom";

    public static final Event APPROVAL_EVENT = new Event("Approval", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event BURN_EVENT = new Event("Burn", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event MINT_EVENT = new Event("Mint", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event SWAP_EVENT = new Event("Swap", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Address>(true) {}));
    ;

    public static final Event SYNC_EVENT = new Event("Sync", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint112>() {}, new TypeReference<Uint112>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected NexiSwapPair(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected NexiSwapPair(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected NexiSwapPair(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected NexiSwapPair(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(APPROVAL_EVENT, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(APPROVAL_EVENT, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.log = log;
                typedResponse.owner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ApprovalEventResponse> approvalEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(APPROVAL_EVENT));
        return approvalEventFlowable(filter);
    }

    public static List<BurnEventResponse> getBurnEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BURN_EVENT, transactionReceipt);
        ArrayList<BurnEventResponse> responses = new ArrayList<BurnEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BurnEventResponse typedResponse = new BurnEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount0 = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount1 = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<BurnEventResponse> burnEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, BurnEventResponse>() {
            @Override
            public BurnEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(BURN_EVENT, log);
                BurnEventResponse typedResponse = new BurnEventResponse();
                typedResponse.log = log;
                typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.amount0 = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount1 = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<BurnEventResponse> burnEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BURN_EVENT));
        return burnEventFlowable(filter);
    }

    public static List<MintEventResponse> getMintEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(MINT_EVENT, transactionReceipt);
        ArrayList<MintEventResponse> responses = new ArrayList<MintEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            MintEventResponse typedResponse = new MintEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount0 = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount1 = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<MintEventResponse> mintEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, MintEventResponse>() {
            @Override
            public MintEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(MINT_EVENT, log);
                MintEventResponse typedResponse = new MintEventResponse();
                typedResponse.log = log;
                typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount0 = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount1 = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<MintEventResponse> mintEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(MINT_EVENT));
        return mintEventFlowable(filter);
    }

    public static List<SwapEventResponse> getSwapEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(SWAP_EVENT, transactionReceipt);
        ArrayList<SwapEventResponse> responses = new ArrayList<SwapEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SwapEventResponse typedResponse = new SwapEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.amount0In = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.amount1In = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.amount0Out = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.amount1Out = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SwapEventResponse> swapEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SwapEventResponse>() {
            @Override
            public SwapEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SWAP_EVENT, log);
                SwapEventResponse typedResponse = new SwapEventResponse();
                typedResponse.log = log;
                typedResponse.sender = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.amount0In = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.amount1In = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.amount0Out = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.amount1Out = (BigInteger) eventValues.getNonIndexedValues().get(3).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SwapEventResponse> swapEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SWAP_EVENT));
        return swapEventFlowable(filter);
    }

    public static List<SyncEventResponse> getSyncEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(SYNC_EVENT, transactionReceipt);
        ArrayList<SyncEventResponse> responses = new ArrayList<SyncEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            SyncEventResponse typedResponse = new SyncEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.reserve0 = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.reserve1 = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<SyncEventResponse> syncEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, SyncEventResponse>() {
            @Override
            public SyncEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(SYNC_EVENT, log);
                SyncEventResponse typedResponse = new SyncEventResponse();
                typedResponse.log = log;
                typedResponse.reserve0 = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.reserve1 = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<SyncEventResponse> syncEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(SYNC_EVENT));
        return syncEventFlowable(filter);
    }

    public static List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.value = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public RemoteFunctionCall<byte[]> DOMAIN_SEPARATOR() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DOMAIN_SEPARATOR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> MINIMUM_LIQUIDITY() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_MINIMUM_LIQUIDITY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<byte[]> PERMIT_TYPEHASH() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PERMIT_TYPEHASH, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<BigInteger> allowance(String param0, String param1) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ALLOWANCE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0), 
                new org.web3j.abi.datatypes.Address(160, param1)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> approve(String spender, BigInteger value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_APPROVE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> balanceOf(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_BALANCEOF, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> burn(String to) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_BURN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> decimals() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_DECIMALS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> factory() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FACTORY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple3<BigInteger, BigInteger, BigInteger>> getReserves() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETRESERVES, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint112>() {}, new TypeReference<Uint112>() {}, new TypeReference<Uint32>() {}));
        return new RemoteFunctionCall<Tuple3<BigInteger, BigInteger, BigInteger>>(function,
                new Callable<Tuple3<BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> initialize(String _token0, String _token1) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INITIALIZE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _token0), 
                new org.web3j.abi.datatypes.Address(160, _token1)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> kLast() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_KLAST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> mint(String to) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_MINT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> name() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NAME, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> nonces(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NONCES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> permit(String owner, String spender, BigInteger value, BigInteger deadline, BigInteger v, byte[] r, byte[] s) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_PERMIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, owner), 
                new org.web3j.abi.datatypes.Address(160, spender), 
                new org.web3j.abi.datatypes.generated.Uint256(value), 
                new org.web3j.abi.datatypes.generated.Uint256(deadline), 
                new org.web3j.abi.datatypes.generated.Uint8(v), 
                new org.web3j.abi.datatypes.generated.Bytes32(r), 
                new org.web3j.abi.datatypes.generated.Bytes32(s)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<BigInteger> price0CumulativeLast() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PRICE0CUMULATIVELAST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> price1CumulativeLast() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_PRICE1CUMULATIVELAST, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> skim(String to) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SKIM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> swap(BigInteger amount0Out, BigInteger amount1Out, String to, byte[] data) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SWAP, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount0Out), 
                new org.web3j.abi.datatypes.generated.Uint256(amount1Out), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.DynamicBytes(data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> symbol() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SYMBOL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> sync() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SYNC, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> token0() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOKEN0, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> token1() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOKEN1, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> totalSupply() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_TOTALSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> transfer(String to, BigInteger value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> transferFrom(String from, String to, BigInteger value) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TRANSFERFROM, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(value)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static NexiSwapPair load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new NexiSwapPair(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static NexiSwapPair load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new NexiSwapPair(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static NexiSwapPair load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new NexiSwapPair(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static NexiSwapPair load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new NexiSwapPair(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<NexiSwapPair> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NexiSwapPair.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<NexiSwapPair> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(NexiSwapPair.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NexiSwapPair> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NexiSwapPair.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<NexiSwapPair> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(NexiSwapPair.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class ApprovalEventResponse extends BaseEventResponse {
        public String owner;

        public String spender;

        public BigInteger value;
    }

    public static class BurnEventResponse extends BaseEventResponse {
        public String sender;

        public String to;

        public BigInteger amount0;

        public BigInteger amount1;
    }

    public static class MintEventResponse extends BaseEventResponse {
        public String sender;

        public BigInteger amount0;

        public BigInteger amount1;
    }

    public static class SwapEventResponse extends BaseEventResponse {
        public String sender;

        public String to;

        public BigInteger amount0In;

        public BigInteger amount1In;

        public BigInteger amount0Out;

        public BigInteger amount1Out;
    }

    public static class SyncEventResponse extends BaseEventResponse {
        public BigInteger reserve0;

        public BigInteger reserve1;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public String from;

        public String to;

        public BigInteger value;
    }
}
