var seckill = {
    URL : {
        now : function () {
            return '/seckill/time/now';
        },
        exposer : function (seckillId) {
            return '/seckill/' + seckillId + '/exposer';
        },
        exection : function (seckillId,md5) {
            return '/seckill/' + seckillId + '/' + md5 + '/execution';
        }
    },
    handleSeckillkill : function (seckillId,seckillBox) {
        //获取秒杀地址，控制现实逻辑，执行秒杀
        seckillBox.hide()
            .html('<button class="btn btn-primary btn-lg" id="killBtn">开始秒杀</button>');
        $.post(seckill.URL.exposer(seckillId),{},function (result) {
            if (result && result['success']) {
                var exposer = result['data'];
                if (exposer['exposed']) {
                    //开启秒杀 ,获取秒杀地址
                    var md5 = exposer['md5'];
                    var killUrl = seckill.URL.exection(seckillId,md5);
                    console.log('killUrl:'+killUrl);
                    //绑定一次点击事件
                    $('#killBtn').one('click',function () {
                        //1.先禁用按钮
                        $(this).addClass('disabled');
                        //2.发送秒杀请求
                        $.post(killUrl,{},function (result) {
                            if (result && result['success']) {
                                var killResult = result['data'];
                                var state = killResult['state'];
                                var stateInfo = killResult['stateInfo'];
                                seckillBox.html('<span class="label label-success">'+stateInfo+'</span>');
                            }
                        });
                    });
                    seckillBox.show();
                } else {
                    var now = exposer['now'];
                    var start = exposer['start'];
                    var end = exposer['end'];
                    seckill.countdown(seckillId,now,start,end);
                }
            }else {
              console.log('result:' + result);
            }
        });
    },
    validatePhone : function (phone) {
      if (phone && phone.length == 11 && !isNaN(phone)) {
          return true;
      }else {
          return false;
      }
    },
    countdown: function (seckillId,nowTime,startTime,endTime) {
        var seckillBox = $('#seckill-box');
        //时间的判断
        if (nowTime > endTime) {
            seckillBox.html('秒杀已结束');
        }
        else  if (nowTime < startTime) {
            var killTime = new Date(startTime);
            seckillBox.countdown(killTime,function (event) {
                var format = event.strftime('秒杀倒计时: %D天 %H时 %M分 %S秒');
                seckillBox.html(format);
                //时间完成后的回调事件
            }).on('finish.countdown',function () {
                //获取秒杀地址，控制现实逻辑，执行秒杀
                seckill.handleSeckillkill(seckillId,seckillBox);
            });
        } else {
            //秒杀开始
            seckill.handleSeckillkill(seckillId,seckillBox);

        }

    },
    detail : {
        init : function (params) {
            //在cookie中查找killPhone
            var killPhone = $.cookie('killPhone');
            if (!seckill.validatePhone(killPhone)){
                var killPhoneModal = $('#killPhoneModal');
                killPhoneModal.modal({
                    show : true, //显示弹出层
                    backdrop : false, //禁止位置关闭
                    keyboard : false //关闭键盘事件
                });
                $('#killPhoneBtn').click(function () {
                    var inputPhone = $('#killPhoneKey').val();
                    if(seckill.validatePhone(inputPhone)) {
                        $.cookie('killPhone',inputPhone,{expires:7,path:'/seckill'});
                        window.location.reload();
                    } else {
                        $('#killPhoneMessage').hide().html('<label class="label label-danger">手机号错误</label>').show();
                    }
                });
            }
            var startTime = params['startTime'];
            var endTime = params['endTime'];
            var seckillId = params['seckillId'];
            //已经登陆，计时交互
            $.get(seckill.URL.now(),{},function (result) {
                if (result && result['success']) {
                    var nowTime = result['data'];
                    //时间判断
                    seckill.countdown(seckillId,nowTime,startTime,endTime);
                } else {
                    console.log('result:'+result);
                }
            });

        }

    }
}