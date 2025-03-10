import { IconButton, IconButtonPropsColorOverrides, Tooltip } from "@mui/material"
import { MouseEventHandler, ReactElement } from "react"
import { OverridableStringUnion } from '@mui/types';

const TooltipedIconButton = (props: {
    tooltipTitle: string,
    onClick?: MouseEventHandler<HTMLButtonElement>,
    color?: OverridableStringUnion<"inherit" 
        | "primary" | "secondary" | "error" | "info" 
        | "success" | "warning" | "default", IconButtonPropsColorOverrides>
    children: ReactElement
}) => {
    const { tooltipTitle, onClick, color } = props;
    return (
        <Tooltip title={tooltipTitle} arrow>
            <IconButton onClick={onClick} color={color}
                sx={{ border: '1px solid rgba(255, 255, 255, 0.23)', borderRadius: '4px', height: '40px' }}>
                {props.children}
            </IconButton>
        </Tooltip>
    )
}

export default TooltipedIconButton;